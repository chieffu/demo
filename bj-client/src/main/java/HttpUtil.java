import com.alibaba.fastjson.TypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class HttpUtil {

    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发起GET请求并返回响应内容
     *
     * @param url 请求URL
     * @return 响应内容字符串
     */
    public static String sendHttpGet(String url) {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return response.getBody();
    }

    /**
     * 发起POST请求并返回响应内容
     *
     * @param url 请求URL
     * @param requestBody 请求体对象（自动序列化为JSON）
     * @return 响应体对象
     */
    public static String sendHttpPost(String url, Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 假设API接受JSON格式请求体

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
    }

    /**
     * 发起PUT请求并返回响应内容
     *
     * @param url 请求URL
     * @param requestBody 请求体对象（自动序列化为JSON）
     * @return 响应体对象
     */
    public static String sendHttpPut(String url, Object requestBody) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 假设API接受JSON格式请求体

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

       return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class).getBody();
    }

    // 可以继续添加其他HTTP方法的方法，如DELETE、PATCH等
}
