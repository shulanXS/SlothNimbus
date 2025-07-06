package com.yupi.yupicturebackend.api.imagesearch;

import com.yupi.yupicturebackend.api.imagesearch.model.ImageSearchResult;
import com.yupi.yupicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.yupi.yupicturebackend.api.imagesearch.sub.GetImageListApi;
import com.yupi.yupicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     * 该方法通过一系列的API调用来搜索和获取与给定图片URL相关的图片列表
     * 它首先获取图片页面URL，然后从该页面URL中提取第一个图片URL，
     * 最后使用这个图片URL来获取一个图片列表
     *
     * @param imageUrl 图片的URL，作为搜索的起点
     * @return 返回一个包含搜索结果的ImageSearchResult对象列表
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        // 根据给定的图片URL获取图片页面URL
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);

        // 从图片页面URL中获取第一个图片URL
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);

        // 使用第一个图片URL获取相关的图片列表
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);

        // 返回图片列表
        return imageList;
    }


    public static void main(String[] args) {
        List<ImageSearchResult> imageList = searchImage("https://www.codefather.cn/logo.png");
        System.out.println("结果列表" + imageList);
    }
}
