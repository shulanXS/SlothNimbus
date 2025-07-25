package com.yupi.yupicturebackend.api.imagesearch.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面地址（step 1）
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取以图搜图页面地址
     *
     * @param imageUrl 图片URL，需要进行urlencode处理
     * @return 以图搜图的结果页面URL
     */
    public static String getImagePageUrl(String imageUrl) {
        // 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        try {
            // 发送请求
            HttpResponse httpResponse = HttpRequest.post(url)
                    .form(formData)
                    .timeout(5000)
                    .execute();
            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String body = httpResponse.body();
            Map<String, Object> result = JSONUtil.toBean(body, Map.class);
            // 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            // 对 URL 进行解码
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (StrUtil.isBlank(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("调用百度以图搜图接口失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }


    /**
     * 主程序入口
     * 演示如何使用以图搜图功能
     *
     * @param args 命令行参数，未使用
     */
    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        // 调用 getImagePageUrl 方法获取搜索结果页面 URL
        String searchResultUrl = getImagePageUrl(imageUrl);
        // 输出搜索成功的消息和结果 URL
        System.out.println("搜索成功，结果 URL：" + searchResultUrl);
    }

}
