package com.yupi.yupicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.DeleteRequest;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.model.dto.user.*;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     * 该接口用于处理用户注册请求，接收用户提交的账户信息，并将信息传递给服务层进行处理
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 检查请求体是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);

        // 从请求体中获取用户账户、密码和确认密码
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 调用用户服务中的注册方法，传入用户账户、密码和确认密码，返回注册结果
        long result = userService.userRegister(userAccount, userPassword, checkPassword);

        // 返回成功响应，包含注册结果
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * 该方法通过POST请求处理用户的登录请求它接收用户账号和密码作为参数，
     * 验证用户信息并返回登录成功的用户信息
     *
     * @param userLoginRequest 用户登录请求对象，包含用户账号和密码
     * @param request HTTP请求对象，用于获取请求相关信息
     * @return 返回一个包含登录用户信息的响应对象
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 检查登录请求对象是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);

        // 从登录请求对象中获取用户账号和密码
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 调用用户服务中的登录方法，传入用户账号、密码和请求对象，获取登录成功的用户信息
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);

        // 返回一个包含登录用户信息的成功响应对象
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     *
     * 该接口用于获取当前登录系统的用户信息它通过HTTP请求获取用户信息，
     * 并返回一个包含用户详细信息的响应对象
     *
     * @param request HTTP请求对象，用于获取当前请求的上下文信息
     * @return 返回一个BaseResponse对象，其中包含登录用户的详细信息（LoginUserVO）
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        // 从请求中获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 将登录用户信息转换为用户视图对象并返回成功响应
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    /**
     * 处理用户添加请求
     * 该方法仅允许具有管理员角色的用户执行
     *
     * @param userAddRequest 包含用户信息的请求体
     * @return 返回一个包含新用户ID的响应对象
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 将请求参数转换为User对象
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);

        // 设置默认密码并加密
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);

        // 插入数据库
        boolean result = userService.save(user);
        // 检查插入结果
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回成功响应
        return ResultUtils.success(user.getId());
    }


    /**
     * 根据用户ID获取用户信息的接口方法（要求调用者具有管理员角色权限）
     *
     * @param id 用户ID，用于查询特定用户信息
     * @return BaseResponse<User> 包含用户信息的响应对象
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        // 校验参数 id 是否有效
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 调用 userService 的 getById 方法获取用户对象
        User user = userService.getById(id);
        // 校验用户对象是否为空，以确定是否找到了对应的用户
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        // 返回成功响应，包含找到的用户对象
        return ResultUtils.success(user);
    }


    /**
     * 根据用户ID获取用户视图对象
     * 该方法首先通过调用getUserById方法获取用户信息，然后将其转换为用户视图对象返回
     * 主要用于需要以视图模型展示用户信息的场景
     *
     * @param id 用户ID，用于指定需要获取视图对象的用户
     * @return 返回一个包含用户视图对象的BaseResponse对象
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        // 调用getUserById方法获取用户信息
        BaseResponse<User> response = getUserById(id);
        // 从响应中提取用户数据
        User user = response.getData();
        // 将用户数据转换为视图对象并返回
        return ResultUtils.success(userService.getUserVO(user));
    }


    /**
     * 删除用户（需要管理员权限才能执行此操作）
     *
     * @param deleteRequest 包含待删除用户ID的请求体如果请求体为空或ID无效，将抛出业务异常
     * @return 返回一个表示删除操作结果的BaseResponse对象
     * @throws BusinessException 当输入参数无效时抛出此异常
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        // 检查删除请求是否为空或ID是否有效，如果无效则抛出业务异常
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用用户服务的removeById方法删除指定ID的用户，并返回删除结果
        boolean b = userService.removeById(deleteRequest.getId());
        // 使用ResultUtils.success方法封装删除结果，并返回给客户端
        return ResultUtils.success(b);
    }

    /**
     * 更新用户信息的接口方法
     * 该方法处理POST请求，路径为/update，需要管理员角色权限
     * 接收一个UserUpdateRequest对象作为请求体，用于更新用户信息
     *
     * @param userUpdateRequest 包含要更新的用户信息的请求对象，不能为空
     * @return 返回一个BaseResponse对象，包含更新操作的结果
     *
     * 首先检查传入的userUpdateRequest及其id是否为空，如果为空则抛出参数错误异常
     * 然后将userUpdateRequest的属性复制到一个新的User对象中
     * 调用userService的updateById方法根据用户ID更新用户信息
     * 如果更新失败，抛出操作错误异常，否则返回成功响应
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        // 检查传入的参数是否为空
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建一个新的User对象，并将请求中的属性复制到该对象中
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 调用服务层方法更新用户信息
        boolean result = userService.updateById(user);
        // 如果更新失败，抛出异常
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回成功响应
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        // 校验查询参数是否为空
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前页码和页面大小
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();

        // 执行用户列表的分页查询
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));

        // 初始化用户封装列表的分页对象
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());

        // 将查询到的用户列表转换为用户封装列表
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);

        // 返回成功响应，包含用户封装列表的分页信息
        return ResultUtils.success(userVOPage);
    }

    /**
     * 兑换会员
     */
    @PostMapping("/exchange/vip")
    public BaseResponse<Boolean> exchangeVip(@RequestBody VipExchangeRequest vipExchangeRequest,
                                             HttpServletRequest httpServletRequest) {
        // 参数校验，如果请求体为空，则抛出参数错误异常
        ThrowUtils.throwIf(vipExchangeRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取会员兑换码
        String vipCode = vipExchangeRequest.getVipCode();

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(httpServletRequest);

        // 调用 service 层的方法进行会员兑换
        boolean result = userService.exchangeVip(loginUser, vipCode);

        // 返回会员兑换结果
        return ResultUtils.success(result);
    }

}
