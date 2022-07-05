package kr.co.changwooinv.wms.network;

import java.util.concurrent.TimeUnit;


import kr.co.changwooinv.wms.BuildConfig;
import kr.co.changwooinv.wms.model.InvItmScanModel;
import kr.co.changwooinv.wms.model.ResultModel;
import kr.co.changwooinv.wms.model.UserInfoModel;
import kr.co.changwooinv.wms.model.WareHouseModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClientService {
    /**
     * 로그인
     * @param proc  프로시져
     * @param user_id 아이디
     * @param pass 비밀번호
     * @param app_version 앱버젼
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<UserInfoModel> postLogin(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String pass,
            @Query("param3") String app_version
    );

    /**
     * 창고, 위치 리스트 조회
     * @param proc  프로시져
     * @param param1 창고(WH), 위치(LC)
     * @param param2 위치 조회시 선택한 창고코드
     */
    @POST("R2JsonProc.asp")
    Call<WareHouseModel> sp_pda_WareLoc_Search(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 입고관리 품목 스캔 insert
     * @param proc  프로시져
     * @param user_id 아이디
     * @param date 일자
     * @param wh_code 창고코드
     * @param loc_code 위치코드
     * @param barcode 품목코드(바코드)
     * @param qty 수량(바코드)
     */
    @POST("R2JsonProc.asp")
    Call<InvItmScanModel> InvItmInsert(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String date,
            @Query("param3") String wh_code,
            @Query("param4") String loc_code,
            @Query("param5") String barcode,
            @Query("param6") int qty
    );

    /**
     * 입고관리 리스트 조회
     * @param proc  프로시져
     * @param user_id 아이디
     * @param date 일자
     * @param wh_code 창고코드
     * @param loc_code 위치코드
     * @param gbn 구분값 (A:리스트, B:상세)
     * @param itm_code B:상세일때 자재코드
     */
    @POST("R2JsonProc.asp")
    Call<InvItmScanModel> InvItmList(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String date,
            @Query("param3") String wh_code,
            @Query("param4") String loc_code,
            @Query("param5") String gbn,
            @Query("param6") String itm_code
    );

    /**
     * 입고관리 리스트 조회
     * @param proc  프로시져
     * @param user_id 아이디
     * @param date 일자
     * @param wh_code 창고코드
     * @param loc_code 위치코드
     * @param itm_code 자재코드
     * @param seq 순번
     */
    @POST("R2JsonProc.asp")
    Call<InvItmScanModel> InvItmDelete(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String date,
            @Query("param3") String wh_code,
            @Query("param4") String loc_code,
            @Query("param5") String itm_code,
            @Query("param6") int seq
    );

    /**
     * 입고관리 리스트 조회
     * @param proc  프로시져
     * @param user_id 아이디
     * @param date 일자
     * @param wh_code 창고코드
     * @param loc_code 위치코드
     */
    @POST("R2JsonProc.asp")
    Call<ResultModel> InvItmCloInsert(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String date,
            @Query("param3") String wh_code,
            @Query("param4") String loc_code
    );


    //로그 찍기
    //태그 OkHttp 입력(adb logcat OkHttp:D *:S)
    // HttpLoggingInterceptor.Level.BODY  모든 바디 로그 온
    // HttpLoggingInterceptor.Level.NONE  로그 오프
    public static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    //타임아웃 1분
    public static final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor);

    //Gson으로 리턴
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder.build())
            .build();

    //String으로 리턴
    public static final Retrofit retrofitString = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_SERVER)
            .addConverterFactory(new ToStringConverterFactory())
            .client(builder.build())
            .build();
}
