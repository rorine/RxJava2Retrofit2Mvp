package com.hxm.rxjava2retrofit2mvp.net.manager;

import com.blankj.utilcode.util.NetworkUtils;
import com.hxm.rxjava2retrofit2mvp.BuildConfig;

import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * Created by Hxm on 2017/2/22.
 * 异常处理
 */

public class NetException {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int ACCESS_DENIED = 302;

    public static ResponseThrowable throwable(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                    ex.message = "请求超时,请检查网络";
                    break;
                case UNAUTHORIZED:
                case FORBIDDEN:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                case ACCESS_DENIED:
                    ex.message = "网络异常";
                    break;
                case NOT_FOUND:
                    ex.message = "HTTP 404";
                    break;
                default:
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        } else if (e instanceof JSONException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = "网络连接失败";
            return ex;
        } else if (e instanceof java.security.cert.CertPathValidatorException) {
            ex = new ResponseThrowable(e, ERROR.SSL_NOT_FOUND);
            ex.message = "证书路径没找到";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时,请稍后重试";
            return ex;
        } else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            if (!NetworkUtils.isConnected()) {
                ex.message = "网络未连接";
            } else if (!NetworkUtils.isAvailableByPing()) {
                ex.message = "网络不可用";
            } else {
                if (BuildConfig.DEBUG) {
                    throw new RuntimeException(e);
                } else {
                    ex.message = "未知错误";
                }
            }
            return ex;
        }
    }

    public static class ResponseThrowable extends Exception {
        public int code;
        public String message;

        public ResponseThrowable(Throwable cause) {
            super(cause);
        }

        public ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;

        }
    }

    /**
     * 约定异常
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;

        /**
         * 证书未找到
         */
        public static final int SSL_NOT_FOUND = 1007;


    }
}
