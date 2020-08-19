package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClassName: ExceptionEnums <br/>
 * Description: TODO
 * Date 2020/4/28 9:02
 *
 * @author Lenovo
 **/
@Getter
@NoArgsConstructor  //创建一个无参构造函数
@AllArgsConstructor //使用后添加一个构造函数，该构造函数含有所有已声明字段属性参数
public enum  ExceptionEnums {
    AGE_CANNOT_BE_NULL(400,"姓名不能为空"),
    CATEGORY_NOT_FOUND(404,"商品分类没有查到"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没有查到"),
    BAND_NOT_FOUND(404,"品牌未查到"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数没有查到"),
    GOOD_STOCK_NOT_FOUND(404,"商品库存没有查到"),
    GOOD_SKU_NOT_FOUND(404,"商品SKU没有查到"),
    GOOD_DETAIL_NOT_FOUND(404,"商品详细没有查找到"),
    Goods_SAVE_ERROR(500,"新增商品失败"),
    Goods_SPU_NOT_FOUND(404,"商品不存在"),
    INVALID_USER_DATA_TYPE(400,"错误的用户数据类型"),
    INVALID_USER_USERNAME_OR_PASSWORD(400,"无效的用户名或密码"),
    UN_AUTHORIZED(400,"未授权"),
    CRATE_TOKEN_ERROR(500,"构造token失败"),
    INVALID_USER_REGISTER_CODE(400,"无效的验证码"),
    BRAND_SAVE_ERROR(500,"服务器新增品牌失败"),
    BRAND_UPDATE_ERROR(500,"品牌修改错误"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(500,"错误的文件上传类型"),
    GOODS_UPDATE_ERROR(500,"更新商品失败"),
    GOODS_ID_CANNOT_BE_NULL(400,"商品id不能为空"),
    CART_NOT_FOUND(404,"购物车为空"),
    ORDER_NOT_FOUND(404,"订单不存在"),
    ORDER_STATUS_NOT_FOUND(404,"订单状态不存在"),
    ORDER_DETAIL_NOT_FOUND(404,"订单详细不存在"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    SECKILL_FAST_ERROR(500,"用户操作频繁"),
    CREATE_ORDER_FAIL(404,"创建订单失败")
    ;
    private Integer code;
    private String msg;

}
