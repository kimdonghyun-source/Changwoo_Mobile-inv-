package kr.co.bang.wms.network;

import java.util.concurrent.TimeUnit;

import kr.co.bang.wms.BuildConfig;
import kr.co.bang.wms.model.CustomerInfoModel;
import kr.co.bang.wms.model.DeliveryOrderModel;
import kr.co.bang.wms.model.EmpModel;
import kr.co.bang.wms.model.InvenModel;
import kr.co.bang.wms.model.InventoryModel;
import kr.co.bang.wms.model.LocationModel;
import kr.co.bang.wms.model.LotItemsModel;
import kr.co.bang.wms.model.MatMoveModel;
import kr.co.bang.wms.model.MaterialLocAndLotModel;
import kr.co.bang.wms.model.MaterialOutDetailModel;
import kr.co.bang.wms.model.MaterialOutListModel;
import kr.co.bang.wms.model.MorEmpModel;
import kr.co.bang.wms.model.MorListModel;
import kr.co.bang.wms.model.MorSerialScan;
import kr.co.bang.wms.model.PalletSnanModel;
import kr.co.bang.wms.model.ResultModel;
import kr.co.bang.wms.model.SerialNumberModel;
import kr.co.bang.wms.model.UserInfoModel;
import kr.co.bang.wms.model.WarehouseModel;
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
     * @param version 앱버전
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<UserInfoModel> postLogin(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String pass,
            @Query("param3") String version
    );


    //---------------------------------------------뱅 시작
    /**
     * 창고 리스트
     * @param proc  프로시저
     * @param gubun 회원/대리점 구분(A : 대리점, C : 회원)
     * @param s_date 조회일자
     * @param out_no 주문자재출고No
     * @param slip_type 전표타입
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MorListModel> morlist(
            @Query("proc") String proc,
            @Query("param1") String gubun,
            @Query("param2") String s_date,
            @Query("param3") String out_no,
            @Query("param4") String slip_type
    );

    /**
     * 주문자재출고 상세 회원
     * @param proc 프로시저
     * @param gubun 회원/대리점 구분(A : 대리점, C : 회원)
     * @param slip_no 주문번호
     * @param slip_type 전표타입
     * */
    @POST("R2JsonProc.asp")
    Call<MorListModel> mordetail(
            @Query("proc") String proc,
            @Query("param1") String gubun,
            @Query("param2") String slip_no,
            @Query("param3") String slip_type
    );


    /**
     * 창고리스트
     * @param proc 프로시저
     * @param code 창고코드
     * */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> morWarehouse(
            @Query("proc") String proc,
            @Query("param1") String code

    );

    /**
     * 재고실사(박스)
     * @param proc 프로시저
     * @param code 시리얼번호
     * */
    @POST("R2JsonProc.asp")
    Call<InvenModel> inventoryBox(
            @Query("proc") String proc,
            @Query("param1") String code

    );

    /**
     * 작업자리스트
     * @param proc 프로시저
     * @param code 명칭
     * */
    @POST("R2JsonProc.asp")
    Call<EmpModel> empList(
            @Query("proc") String proc,
            @Query("param1") String code

    );


    /**
     * 창고이동 시리얼 스캔
     * @param proc 프로시저
     * @param code 코드
     * */
    @POST("R2JsonProc.asp")
    Call<MatMoveModel> movescan(
            @Query("proc") String proc,
            @Query("param1") String code

    );

    /**
     * 창고 리스트
     * @param proc
     * @param code 창고코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> posthouse(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     *재고실사저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_mod_save.asp")
    Call<InvenModel> matModSave(
            @Body RequestBody body
    );

    /**
     *창고이동처리
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_move_save.asp")
    Call<ResultModel> postSendMatMove(
            @Body RequestBody body
    );

    /**
     *주문자재출고 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_save.asp")
    Call<ResultModel> postSendWareSave(
            @Body RequestBody body
    );

    /**
     *주문자재출고 출고승인(주문)
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_cmf_order.asp")
    Call<ResultModel> postSendWareAgree(
            @Body RequestBody body
    );

    /**
     *주문자재출고 출고승인(AS)
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_cmf_as.asp")
    Call<ResultModel> postSendWareAS(
            @Body RequestBody body
    );

    /**
     *주문자재출고 출고승인(대리점)
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_cmf_order.asp")
    Call<ResultModel> postSendWareStore(
            @Body RequestBody body
    );

    /**
     * 자재출고피킹 시리얼번호 스캔
     * @param proc  프로시저
     * @param serial_no 시리얼번호
     * @param wh_code 입고처
     * @param corp_code 사업장
     * @param out_date 출고일자
     * @param out_no 출고순번
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MorSerialScan> morSerialScan(
            @Query("proc") String proc,
            @Query("param1") String serial_no,
            @Query("param2") String wh_code,
            @Query("param3") String corp_code,
            @Query("param4") String out_date,
            @Query("param5") String out_no
    );

    //---------------------------------------------뱅 끝

    /**
     * 창고 리스트
     * @param proc
     * @param type 창고타입(M : 원자재, P : 완제품)
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> postWarehouse(
            @Query("proc") String proc,
            @Query("param1") String type
    );

    /**
     * 로케이션 리스트
     * @param proc
     * @param code 창고코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postLocation(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 로케이션 리스트
     * @param proc
     * @param code 창고코드
     * @param type 창고타입(M : 원자재, P : 완제품)
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postScanLocation(
            @Query("proc") String proc,
            @Query("param1") String code,
            @Query("param2") String type
    );

    /**
     * 창고와 로케이션 정보 조회
     * @param proc 프로시져
     * @param code 로케이션 코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postWarehouseLocation(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 로트 재고정보
     * @param proc 프로시져
     * @param code 로케이션코드
     * @param lot 로트번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LotItemsModel> postLotItems(
            @Query("proc") String proc,
            @Query("param1") String code,
            @Query("param2") String lot
    );

    //로케이션 이동
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_etc_save.asp")
    Call<ResultModel> postSendLocation(
            @Body RequestBody body
    );

    /**
     * 팔레트 바코드 스캔(시리얼번호)
     * @param proc 프로시져
     * @param barcode 스캔 바코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<PalletSnanModel> postScanPallet(
            @Query("proc") String proc,
            @Query("param1") String barcode
    );

    //제품입고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_sin_save.asp")
    Call<ResultModel> postSendProductionIn(
            @Body RequestBody body
    );

    /**
     * 자재불출 지시조회
     * @param proc 프로시져
     * @param param1 불출지시 일자
     * @param param2 불출창고 코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialOutListModel> postOutOrderList(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 자재불출 지시 상세
     * @param proc 프로시져
     * @param param1 자재불출번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialOutDetailModel> postOutOrderDetail(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    /**
     * 자재불출 로케이션, 시스템로트 스캔
     * @param proc 프로시져
     * @param param1 불출창고코드
     * @param param2 로케이션코드
     * @param param3 시스템로트번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialLocAndLotModel> postOutLocAndLot(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2,
            @Query("param3") String param3
    );

    /**
     * 자재불출 지시 상세
     */
    //제품출고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_out_save.asp")
    Call<ResultModel> postMaterialSend(
            @Body RequestBody body
    );

    /**
     * 출고지시서 상세
     * @param proc 프로시져
     * @param param1 출고지시번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<CustomerInfoModel> postShipReqList(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 출고지시서 상세
     * @param proc 프로시져
     * @param param1 출고지시번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<DeliveryOrderModel> postShipReqDetail(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    //제품출고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_ship_save.asp")
    Call<ResultModel> postSendProductionOut(
            @Body RequestBody body
    );

    /**
     * 파레트관리 분할 전표생성
     * @param proc 프로시져
     * @param param1 시리얼번호
     * @param param2 품목코드
     * @param param3 창고코드
     * @param param4 로케이션코드
     * @param param5 원수량(재고수량)
     * @param param6 분할수량
     * @param param7 로그인ID
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<SerialNumberModel> postMakeBunhalJunphyo(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2,
            @Query("param3") String param3,
            @Query("param4") String param4,
            @Query("param5") String param5,
            @Query("param6") String param6,
            @Query("param7") String param7
    );

    //파레트관리 병합 전표 생성
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_plt_mrg_save.asp")
    Call<SerialNumberModel> postMakeMergeJunphyo(
            @Body RequestBody body
    );

    /**
     * 재고실사 상세
     * @param proc 프로시져
     * @param param1 재고실사번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<InventoryModel> postInventoryScan(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    //재고실사 전표 생성
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_mod_save.asp")
    Call<ResultModel> postSendInventory(
            @Body RequestBody body
    );

    //R2JsonProc_plt_mrg_save.asp

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