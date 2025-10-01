package com.akshay.ecommerce.constants;

import java.util.EnumSet;

public enum fromStatusEnum {
    ORDER_PLACED(EnumSet.of(toStatusEnum.CANCELLED, toStatusEnum.ORDER_CONFIRMED, toStatusEnum.ORDER_REJECTED)),
    CANCELLED(EnumSet.of(toStatusEnum.REFUND_INITIATED, toStatusEnum.CLOSED)),
    ORDER_REJECTED(EnumSet.of(toStatusEnum.REFUND_INITIATED, toStatusEnum.CLOSED)),
    ORDER_CONFIRMED(EnumSet.of(toStatusEnum.CANCELLED, toStatusEnum.ORDER_SHIPPED)),
    ORDER_SHIPPED(EnumSet.of(toStatusEnum.DELIVERED)),
    DELIVERED(EnumSet.of(toStatusEnum.RETURN_REQUESTED, toStatusEnum.CLOSED)),
    RETURN_REQUESTED(EnumSet.of(toStatusEnum.RETURN_REJECTED, toStatusEnum.RETURN_APPROVED)),
    RETURN_REJECTED(EnumSet.of(toStatusEnum.CLOSED)),
    RETURN_APPROVED(EnumSet.of(toStatusEnum.PICK_UP_INITIATED)),
    PICK_UP_INITIATED(EnumSet.of(toStatusEnum.PICK_UP_COMPLETED)),
    PICK_UP_COMPLETED(EnumSet.of(toStatusEnum.REFUND_INITIATED)),
    REFUND_INITIATED(EnumSet.of(toStatusEnum.REFUND_COMPLETED)),
    REFUND_COMPLETED(EnumSet.of(toStatusEnum.CLOSED));
    <E extends Enum<E>> fromStatusEnum(EnumSet<E> enumConstructor) {

    }

}