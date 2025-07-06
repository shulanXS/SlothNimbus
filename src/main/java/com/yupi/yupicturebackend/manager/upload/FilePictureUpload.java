package com.yupi.yupicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 文件图片上传
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {

    /**
     * 验证图片文件的合法性
     * 此方法主要用于确保上传的文件是符合规范的图片文件，包括检查文件是否存在、文件大小是否超过限制以及文件格式是否允许
     *
     * @param inputSource 输入的文件源，预期是一个MultipartFile对象，即通过HTTP请求上传的文件
     */
    @Override
    protected void validPicture(Object inputSource) {
        // 将输入源对象转换为MultipartFile类型
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 检查文件是否为空
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");

        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");

        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀列表（或者集合）
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 重写获取原始文件名的方法
     *
     * @param inputSource 输入源，此处特指multipart文件对象
     * @return 返回文件的原始名称
     */
    @Override
    protected String getOriginFilename(Object inputSource) {
        // 将输入源对象转换为MultipartFile类型
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 调用方法获取并返回文件的原始名称
        return multipartFile.getOriginalFilename();
    }

    /**
     * 重写processFile方法以处理文件上传
     * 该方法接收一个输入源对象和一个文件对象，负责将输入源对象转换为MultipartFile类型，并将其内容传输到指定文件中
     *
     * @param inputSource 输入源对象，预期为MultipartFile类型，用于接收上传的文件数据
     * @param file 目标文件，输入源内容将被传输到这个文件中
     * @throws Exception 如果文件传输过程中发生错误，则抛出异常
     */
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        // 将输入源对象转换为MultipartFile类型，以便进行文件操作
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 将MultipartFile的内容传输到指定的文件中，完成文件上传的过程
        multipartFile.transferTo(file);
    }
}
