package club.ihere.wechat.common.exception;

/**
 * @author: fengshibo
 * @date: 2018/11/2 11:20
 * @description:
 */
public class WechatException extends BaseException {

    private static final long serialVersionUID = 6511252697679361658L;

    public WechatException() {
        super();
    }

    public WechatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WechatException(String message) {
        super(message);
    }

    public WechatException(Throwable cause) {
        super(cause);
    }

}
