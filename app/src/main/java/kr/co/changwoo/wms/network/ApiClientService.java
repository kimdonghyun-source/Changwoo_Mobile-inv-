package kr.co.changwoo.wms.network;

import java.util.concurrent.TimeUnit;


import kr.co.changwoo.wms.BuildConfig;
import kr.co.changwoo.wms.model.ItmModel;
import kr.co.changwoo.wms.model.ShipBoxModel;
import kr.co.changwoo.wms.model.ShipDetailModel;
import kr.co.changwoo.wms.model.ShipNoModel;
import kr.co.changwoo.wms.model.ShipScanModel;
import kr.co.changwoo.wms.model.SinCstModel;
import kr.co.changwoo.wms.model.SinListModel;
import kr.co.changwoo.wms.model.UserInfoModel;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
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
     * 거래처리스트
     * @param proc  프로시져
     * @param param1 빈값
     */
    @POST("R2JsonProc.asp")
    Call<SinCstModel> sp_pda_cst_code(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    /**
     * 입고관리 바코드스캔
     * @param proc  프로시져
     * @param mac 맥주소
     * @param get_date 일자
     * @param barcode 바코드
     * @param qty 수량
     */
    @POST("R2JsonProc.asp")
    Call<SinListModel> sp_pda_itm_chk(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") String barcode,
            @Query("param4") int qty
    );


    /**
     * 입고관리 바코드 삭제
     * @param proc  프로시져
     * @param mac macadress
     * @param get_date 일자
     * @param no 순번
     */
    @POST("R2JsonProc.asp")
    Call<SinListModel> sp_pda_itm_del(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") int no
    );

    /**
     * 입고내역 재조회
     * @param proc  프로시져
     * @param mac macadress
     * @param get_date 일자
     */
    @POST("R2JsonProc.asp")
    Call<SinListModel> sp_pda_itm_search(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date
    );

    /**
     * 입고마감
     * @param proc  프로시져
     * @param mac macadress
     * @param get_date 일자
     * @param cst_code 거래처코드
     * @param user_id 담당
     * @param etc 비고사항
     */
    @POST("R2JsonProc.asp")
    Call<SinListModel> sp_pda_sin_clo(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") String cst_code,
            @Query("param4") String user_id,
            @Query("param5") String etc
    );

    /**
     * 출하관리 바코드스캔(12자리)
     * @param proc  프로시져
     * @param mac
     * @param time
     * @param barcode
     */
    @POST("R2JsonProc.asp")
    Call<ShipNoModel> sp_pda_ship_scan(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String time,
            @Query("param3") String barcode
    );

    /**
     * 출하관리 의뢰순번 스캔(16자리)
     * @param proc  프로시져
     * @param mac  맥주소
     * @param date 일자
     * @param ship_date 출하의뢰일자
     * @param ship_no 출하의뢰순번
     * @param barcode 바코드
     */
    @POST("R2JsonProc.asp")
    Call<ShipScanModel> sp_pda_itm_req(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String date,
            @Query("param3") String ship_date,
            @Query("param4") String ship_no,
            @Query("param5") String barcode
    );

    /**
     * 출하관리 의뢰순번 스캔(16자리이상 BOx포장지)
     * @param proc  프로시져
     * @param mac  맥주소
     * @param date 일자
     * @param ship_date 출하의뢰일자
     * @param ship_no 출하의뢰순번
     * @param barcode 바코드
     * @param qty   수량
     */
    @POST("R2JsonProc.asp")
    Call<ShipScanModel> sp_pda_itm_name(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String date,
            @Query("param3") String ship_date,
            @Query("param4") String ship_no,
            @Query("param5") String barcode,
            @Query("param6") int qty
    );

    /**
     * BOX_NO 등록 조회
     * @param proc  프로시져
     * @param mac 맥주소
     * @param get_date 일자
     * @param ship_date 출하의뢰일자
     * @param ship_no 출하의뢰순번
     */
    @POST("R2JsonProc.asp")
    Call<ShipBoxModel> sp_pda_ship_box_read(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") String ship_date,
            @Query("param4") String ship_no
    );

    /**
     * 팝업 디테일 조회
     * @param proc  프로시져
     * @param userid 로그인아이디
     * @param get_date 처리일자
     * @param ship_date 출하의뢰일자
     * @param ship_no 출하의뢰순번
     */
    @POST("R2JsonProc.asp")
    Call<ShipBoxModel> sp_pda_pop_ship_list(
            @Query("proc") String proc,
            @Query("param1") String userid,
            @Query("param2") String get_date,
            @Query("param3") String ship_date,
            @Query("param4") String ship_no
    );

    /**
     * 출하관리 바코드 삭제
     * @param proc  프로시져
     * @param mac macadress
     * @param get_date 일자
     * @param no 순번
     */
    @POST("R2JsonProc.asp")
    Call<ShipBoxModel> sp_pda_sreqdel(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") int no
    );

    /**
     * BOX_NO 등록 조회
     * @param proc  프로시져
     * @param mac 맥주소
     * @param get_date 일자
     * @param ship_date 출하의뢰일자
     * @param ship_no 출하의뢰순번
     */
    @POST("R2JsonProc.asp")
    Call<ShipDetailModel> sp_pda_ship_list(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") String ship_date,
            @Query("param4") String ship_no

    );

    /**
     * 출하등록
     * @param proc  프로시져
     * @param mac macadress
     * @param get_date 일자
     * @param ship_date 출하의뢰일자
     * @param ship_no   출하의뢰순번
     * @param emp_code  사원코드
     */
    @POST("R2JsonProc.asp")
    Call<ShipDetailModel> sp_pda_ship_clo(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String get_date,
            @Query("param3") String ship_date,
            @Query("param4") String ship_no,
            @Query("param5") String emp_code
    );

    /**
     * 품목관리(조회)
     * @param proc  프로시져
     * @param userid 유저아이디
     * @param get_date 일자
     *
     */
    @POST("R2JsonProc.asp")
    Call<ItmModel> sp_pda_list_in(
            @Query("proc") String proc,
            @Query("param1") String userid,
            @Query("param2") String get_date
    );

    /**
     * 품목관리(의뢰품명스캔)
     * @param proc  프로시져
     * @param userid 유저아이디
     * @param get_date 일자
     * @param barcode 바코드명
     * @param qty 수량
     *
     */
    @POST("R2JsonProc.asp")
    Call<ItmModel> sp_pda_itm_in(
            @Query("proc") String proc,
            @Query("param1") String userid,
            @Query("param2") String get_date,
            @Query("param3") String barcode,
            @Query("param4") int qty

    );

    /**
     * 품목관리 삭제
     * @param proc  프로시져
     * @param userid 아이디
     * @param get_date 일자
     * @param no 순번
     */
    @POST("R2JsonProc.asp")
    Call<ItmModel> sp_pda_itm_del_in(
            @Query("proc") String proc,
            @Query("param1") String userid,
            @Query("param2") String get_date,
            @Query("param3") int no
    );

    /**
     * 품목마감
     * @param proc  프로시져
     * @param userid 아이디
     * @param get_date 일자
     * @param text  비고
     */
    @POST("R2JsonProc.asp")
    Call<ItmModel> sp_pda_clo_in(
            @Query("proc") String proc,
            @Query("param1") String userid,
            @Query("param2") String get_date,
            @Query("param3") String text
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
