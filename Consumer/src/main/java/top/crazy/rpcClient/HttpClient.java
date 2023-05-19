package top.crazy.rpcClient;


import org.apache.commons.io.IOUtils;
import top.crazy.common.Invocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.rpcClient
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  20:20
 * @Description: 发送http 请求
 */
public class HttpClient {

    public String send(String hostname, Integer port, Invocation invocation){

        try {
            URL url = new URL("http",hostname,port,"/");
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            //配置

            OutputStream outputStream = httpURLConnection.getOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(outputStream);

            oos.writeObject(invocation);

            oos.flush();

            oos.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            String result = IOUtils.toString(inputStream);
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
        return "null";
    }
}
