package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.Artifact;
import com.googlecode.totallylazy.Callable1;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class S3Connector implements Callable1<Artifact, URLConnection>{
    public static final String AUTHORIZATION = "Authorization";
    public static Pattern http = compile("http\\:\\/\\/([^\\/]+)\\.s3\\.amazonaws\\.com/");

    private final AwsCredentials awsCredentials;


    public S3Connector(final AwsCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }

    public static  S3Connector s3ConnectionUsing(final AwsCredentials awsCredentials) {
        return new S3Connector(awsCredentials);
    }

    @Override
    public URLConnection call(Artifact artifact) throws Exception {
        byte[] keyBytes = awsCredentials.secretKey().getBytes("UTF8");
        final SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        final Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return get(mac,http.matcher(artifact.url().toString()).replaceFirst("/$1/"));

    }

    public String sign(final Mac mac, String data) throws Exception
    {
        return Base64.encode(mac.doFinal(data.getBytes("UTF8")));
    }

    public HttpURLConnection get(final Mac mac, String s3Item) throws Exception
    {

        String method = "GET";
        String date = s3TimestampPattern().format(new Date()) + "GMT";

        URL url = new URL("http","s3.amazonaws.com",80, s3Item);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false);
        httpConn.setDefaultUseCaches(false);
        httpConn.setAllowUserInteraction(true);
        httpConn.setRequestMethod(method);
        httpConn.setRequestProperty("Date", date);
        httpConn.setRequestProperty("Content-Length", "0");
        httpConn.setRequestProperty(AUTHORIZATION, "AWS " + awsCredentials.accessKeyId() + ":" + createSignature(mac, method, date, s3Item));
        return httpConn;
    }

    private String createSignature(Mac mac, String method, String date, String bucket) throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append(method).append("\n");
        buf.append("").append("\n");
        buf.append("").append("\n");
        buf.append(date).append("\n");
        buf.append(bucket);
        return sign(mac, buf.toString()).trim();
    }

    private SimpleDateFormat s3TimestampPattern() {
        String fmt = "EEE, dd MMM yyyy HH:mm:ss ";
        SimpleDateFormat df = new SimpleDateFormat(fmt, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }
}
