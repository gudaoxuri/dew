package groop.idealworld.dew.ossutils.handle;


/**
 * @author yiye
 * @date 2022/4/1
 * @description
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
