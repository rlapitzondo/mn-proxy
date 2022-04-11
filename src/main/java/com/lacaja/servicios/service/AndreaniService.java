package com.lacaja.servicios.service;

import io.micronaut.http.client.HttpClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Raul Lapitzondo raul.lapitzondo@gmail.com -
 * Area de Arquitectura La Caja
 * @Date 9/4/22 17:02
 * @project mn-proxy
 * @Version 1.0
 */
@Singleton
public class AndreaniService {

    private static final Logger log = LoggerFactory.getLogger(AndreaniService.class);
    private static final String X_AUTHORIZATION_TOKEN = "X-Authorization-token";


    @Inject
    private HttpClient httpClient;

    private  String URL = "https://apis.andreani.com";
    private  String CREATE_ORDER = "/v2/ordenes-de-envio";
    private  String ETIQUETA = "/v2/ordenes-de-envio/numeroAndreani/etiquetas";

    private  String URI_TOKEN = "/login";
    private  String TOKEN_BASIC = "Z21yYV9nbGE6anFjbm5CRUM2TQ==";

    private String TOKEN;

    public String postCreateOrder(String pBody) {
        String token = getToken();
        System.out.println("token: " + token);

        return createOrderService( pBody,  token);
    }


    public String getToken(){
        Response response = null;
        String tokenAndreani = null;
        Long inicio = Calendar.getInstance().getTimeInMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(URL + URI_TOKEN)
                .method("GET", null)
                .addHeader("Authorization", "Basic " + TOKEN_BASIC)
                .build();
        try {
            response = client.newCall(request).execute();
            if( null != response && response.code() == 200 ) {
                tokenAndreani= response.header(X_AUTHORIZATION_TOKEN);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long fin = (Calendar.getInstance().getTimeInMillis() - inicio);
        System.out.println("Fin getToken() - Tiempo Rpta: " + fin);

        return tokenAndreani;
    }

    public byte[] getEtiquetaService(String id){
        String uri = URL + ETIQUETA.replaceAll(" ", "%20").replace("numeroAndreani", id);
        Response response = null;
        byte[]  resultByte = null;
        String token = getToken();

        Long inicio = Calendar.getInstance().getTimeInMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(uri)
                .method("GET", null)
                .addHeader("x-authorization-token", token)
                .addHeader("Content-Type", "application/pdf")
                .build();
        try {
            response = client.newCall(request).execute();
            if(null != response && (response.code() == 200 || response.code() == 202)) {
                ResponseBody responseBody = response.body();
                final byte[] data = new byte[1024];
                final ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
                InputStream input = responseBody.byteStream();
                int len ;
                while (true){
                    len = input.read(data);
                    if(  len == 1 ){
                        break;
                    }
                    tmpOut.close();
                    ByteBuffer byteBufferResponse = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());
                    byte[] responseArray = byteBufferResponse.array();
                    resultByte = org.apache.commons.codec.binary.Base64.encodeBase64(responseArray);

                }
            }else{
                resultByte = response.message().getBytes(StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long fin = (Calendar.getInstance().getTimeInMillis() - inicio);
        System.out.println("Fin getToken() - Tiempo Rpta: " + fin);

        return resultByte;
    }


    public String createOrderService(String json, String token){
        Response response = null;
        String result = null;
        Long inicio = Calendar.getInstance().getTimeInMillis();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(json, mediaType);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(URL + CREATE_ORDER)
                .method("POST", body)
                .addHeader("x-authorization-token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            response = client.newCall(request).execute();
            if(null != response && (response.code() == 200 || response.code() == 202)) {
                result = convertInputStreamToString(response.body());
            }else{
                result = response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long fin = (Calendar.getInstance().getTimeInMillis() - inicio);
        System.out.println("Fin getToken() - Tiempo Rpta: " + fin);
        return result;
    }

    private String convertInputStreamToString(ResponseBody rpBody) {
        InputStream st = rpBody != null ? rpBody.byteStream() : null;
        String stResult = null;
        if( null != st){
            Stream<String> streamString = new BufferedReader( new InputStreamReader( st, StandardCharsets.UTF_8 )).lines();
            if (null != streamString){
                stResult = streamString.collect(Collectors.joining("/n"));
            }
        }
        return stResult;
    }
}
