package com.ycjt.rongcloudim.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.ycjt.rongcloudim.common.ErrorCode;
import com.ycjt.rongcloudim.common.NetConstant;
import com.ycjt.rongcloudim.common.ThreadManager;
import com.ycjt.rongcloudim.model.Resource;
import com.ycjt.rongcloudim.model.Result;

public abstract class NetworkOnlyResource<ResultType,RequestType> {
    private final ThreadManager threadManager;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkOnlyResource() {
        this.threadManager = ThreadManager.getInstance();
        if(threadManager.isInMainThread()) {
            init();
        }else {
            threadManager.runOnUIThread(() -> init());
        }

    }
    private void init(){
        result.setValue(Resource.loading(null));
        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        LiveData<RequestType> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            if (response != null) {
                if(response instanceof Result){
                    int code = ((Result)response).code;
                    if(code != NetConstant.REQUEST_SUCCESS_CODE){
                        result.setValue(Resource.error(code, null));
                        return;
                    } else {
                        // do nothing
                    }
                }
                threadManager.runOnWorkThread(() -> {
                    ResultType resultType = transformRequestType(response); //自定义的
                    if(resultType == null){
                        resultType = transformDefault(response); //默认
                    }
                    saveCallResult(resultType);
                    result.postValue(Resource.success(resultType));
                });
            } else {
                result.setValue(Resource.error(ErrorCode.API_ERR_OTHER.getCode(), null));
            }
        });
    }

    /**
     * 重写此方法完成请求类型和响应类型转换
     * 如果是请求结果是 Result<ResultType>  类型则不用重写
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType transformRequestType(RequestType response){
        return null;
    }

    @WorkerThread
    private ResultType transformDefault(RequestType response){
        if(response instanceof Result){
            Object result = ((Result) response).getResult();
            if(result != null){
                try {
                    return  (ResultType)result;
                } catch (Exception e){
                    return null;
                }
            }else {
                return null;
            }
        }else{
            return null;
        }
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected void saveCallResult(@NonNull ResultType item){
    }

    @NonNull
    @MainThread
    protected abstract LiveData<RequestType> createCall();
}