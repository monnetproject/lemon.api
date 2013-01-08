/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *******************************************************************************
 */
package eu.monnetproject.lemon.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author John McCrae
 */
public class HttpAuthenticate {
//
//    private static final Charset UTF8 = Charset.forName("UTF-8");
//
//    public static String digestAuth(final String algorithm, final String realm, final String nonce, String username, String password, final String qop, String path, final String opaque) throws IOException {
//        String auth;
//        final MessageDigest md;
//        try {
//            md = MessageDigest.getInstance(algorithm != null ? algorithm : "MD5");
//        } catch(NoSuchAlgorithmException x) {
//            throw new IOException("Unsupported algorithm from server", x);
//        }
//        if (realm == null || nonce == null) {
//            throw new IOException("Authentication required via digest but server did not provide realm or nonce");
//        }
//        final String HA1 = new BigInteger(1,md.digest((username + ":" + realm + ":" + password).getBytes(UTF8))).toString(16);
//        final String HA2;
//        if(qop != null && qop.matches("auth-int.*")) {
//            throw new IOException("auth-int not supported by this method");
//        } else {
//            HA2 = new BigInteger(1,md.digest(("GET:" + path).getBytes(UTF8))).toString(16);
//        }
//        final CNonce cnonce = CNonce.getNonce(HA1);
//        final String response;
//        if(qop != null && qop.matches("auth,?.*")) {
//            response = new BigInteger(1,md.digest((HA1 + ":" + nonce + ":"  + cnonce.ncString() + ":" + cnonce.nonce + ":"+ qop + ":" + HA2).getBytes(UTF8))).toString(16);
//        } else {
//            response = new BigInteger(1,md.digest((HA1 + ":" + nonce + ":" + HA2).getBytes(UTF8))).toString(16);
//        }
//        final StringBuilder authBuilder = new StringBuilder("username=\"").append(username).append("\", ");
//        authBuilder.append("realm=\"").append(realm).append("\", ");
//        authBuilder.append("nonce=\"").append(nonce).append("\", ");
//        authBuilder.append("uri=\"").append(path).append("\", ");
//        authBuilder.append("response=\"").append(response).append("\", ");
//        authBuilder.append("qop=").append(qop).append(", ");
//        authBuilder.append("nc=").append(cnonce.ncString()).append(", ");
//        authBuilder.append("cnonce=\"").append(cnonce.nonce).append("\", ");
//        authBuilder.append("algorithm=\"").append(algorithm).append("\", ");
//        authBuilder.append("opaque=\"").append(opaque).append("\"");
//        auth = authBuilder.toString();
//        return auth;
//    }

    private HttpAuthenticate() {
    }

    public static InputStream connectAuthenticate(URL url, String username, String password) throws IOException {
        if (username == null || password == null) {
            return url.openStream();
        } else {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort() > 0 ? url.getPort() : 80),
                    new UsernamePasswordCredentials(username, password));
            HttpGet get = new HttpGet(url.toString());

            return httpclient.execute(get).getEntity().getContent();
        }
    }
    
    public static InputStream postAuthenticate(URL url, String username, String password, Map<String,String> parameters) throws IOException {
        if (username == null || password == null) {
            final URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("POST");
            } else if(conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection)conn).setRequestMethod("POST");
            } else {
                System.err.println("Attempting to send SPARQL Update to non-HTTP address: " + url);
            }
            final DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            final StringBuffer content = new StringBuffer();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                if(content.length() > 0) {
                    content.append("&");
                }
                content.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(),"UTF-8"));
            }
            out.writeBytes(content.toString());
            out.flush();
            out.close();
            return conn.getInputStream();
        } else {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort() > 0 ? url.getPort() : 80),
                    new UsernamePasswordCredentials(username, password));
            HttpPost post = new HttpPost(url.toString());
            final List<NameValuePair> ps = new LinkedList<NameValuePair>();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                ps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(ps));

            return httpclient.execute(post).getEntity().getContent();
        }
    }
//    
//    public static InputStream connectAuthenticate2(URLConnection connection, String username, String password) throws IOException {
//        if (connection instanceof HttpURLConnection) {
//            final HttpURLConnection conn = (HttpURLConnection) connection;
//            final int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                return conn.getInputStream();
//            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                if (username == null || password == null) {
//                    throw new IOException("Authentication required (Server returned 401) but no username or password available");
//                }
//                final List<String> wwwAuths = conn.getHeaderFields().get("WWW-Authenticate");
//                if (wwwAuths == null || wwwAuths.isEmpty()) {
//                    System.err.println("*******"+"no wwwauth");
//                    return conn.getInputStream();
//                }
//                final String wwwAuth = wwwAuths.get(0);
//                System.out.println("*******"+wwwAuth);
//                final String realm = getParam(wwwAuth, "realm");
//                final String qop = getParam(wwwAuth, "qop");
//                final String nonce = getParam(wwwAuth, "nonce");
//                final String opaque = getParam(wwwAuth, "opaque");
//                final String algorithm = getParam(wwwAuth, "algorithm");
//                final String auth;
//                final String authMethod;
//                if (wwwAuth.startsWith("Basic")) {
//                    // Basic Authentication
//                    // See: http://en.wikipedia.org/wiki/Basic_access_authentication
//                    authMethod = "Basic";
//                    auth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
//                } else if (wwwAuth.startsWith("Digest")) {
//                    // Digest Authentication
//                    // See: http://en.wikipedia.org/wiki/Digest_access_authentication
//                    authMethod = "Digest";
//                System.out.println("*******dIGEST");
//                    final String path = connection.getURL().getFile();
//                    auth = digestAuth(algorithm, realm, nonce, username, password, qop, path, opaque);
//                } else {
//                    throw new IOException("Unsupported authentication from server: " + wwwAuth);
//                }
//                final URLConnection authConn = conn.getURL().openConnection();
//                authConn.setDoOutput(true);
//                authConn.setDoInput(true);
//                System.err.println("*******"+authMethod + " " + auth);
//                authConn.setRequestProperty("Authorization", authMethod + " " + auth);
//                authConn.setRequestProperty("test", "test");
//                return authConn.getInputStream();
//            } else {
//                // Hope or trigger the right IOException 
//                return conn.getInputStream();
//            }
//        } else {
//            return connection.getInputStream();
//        }
//    }
//
//    private static String getParam(String s, String paramName) {
//        final Matcher m = Pattern.compile(paramName + "\\s*=\\s*\"([^\"]*)\"").matcher(s);
//        if (m.find()) {
//            return m.group(1);
//        } else {
//            return null;
//        }
//    }
//
//    private static class CNonce {
//
//        public final int nc;
//        public final String nonce;
//        private static final Random random = new Random();
//        private static final HashMap<String, CNonce> nonces = new HashMap<String, CNonce>();
//
//        private CNonce() {
//            this.nc = 1;
//            this.nonce = Integer.toHexString(Math.abs(random.nextInt()));
//        }
//
//        private CNonce(CNonce c) {
//            this.nc = c.nc + 1;
//            this.nonce = c.nonce;
//        }
//
//        public static CNonce getNonce(String key) {
//            if (!nonces.containsKey(key)) {
//                final CNonce nonce = new CNonce();
//                nonces.put(key, nonce);
//                return nonce;
//            } else {
//                final CNonce nonce = new CNonce(nonces.get(key));
//                nonces.put(key, nonce);
//                return nonce;
//            }
//        }
//        private static final DecimalFormat format = new DecimalFormat("00000000");
//
//        public String ncString() {
//            return format.format(nc);
//        }
//    }
}
