package group.idealworld.dew.ossutils.handle;


/**
 * @param <T> 客户端类型
 * @author yiye
 **/
public class DewOssHandleClient<T> {
    private T ossClient;

    public T getOssClient() {
        return this.ossClient;
    }

    public void setOssClient(T ossClient) {
        this.ossClient = ossClient;
    }

}
