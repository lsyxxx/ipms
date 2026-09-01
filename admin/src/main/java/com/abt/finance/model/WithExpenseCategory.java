package com.abt.finance.model;

/**
 * 携带费用分类快照能力的业务实体标记接口
 */
public interface WithExpenseCategory {

    ExpenseCategoryRef getExpenseCategoryRef();

    void setExpenseCategoryRef(ExpenseCategoryRef ref);

    /**
     * 仅请求入参，保存时用于查主数据，不落库
     */
    String getExpenseCategoryId();

    void setExpenseCategoryId(String id);

    default String getExpenseCategoryCode() {
        ExpenseCategoryRef ref = getExpenseCategoryRef();
        return ref != null ? ref.getCode() : null;
    }

    default void setExpenseCategoryCode(String code) {
        ensureRef().setCode(code);
    }

    default String getExpenseCategoryName() {
        ExpenseCategoryRef ref = getExpenseCategoryRef();
        return ref != null ? ref.getName() : null;
    }

    default void setExpenseCategoryName(String name) {
        ensureRef().setName(name);
    }

    default ExpenseCategoryRef ensureRef() {
        ExpenseCategoryRef ref = getExpenseCategoryRef();
        if (ref == null) {
            ref = new ExpenseCategoryRef();
            setExpenseCategoryRef(ref);
        }
        return ref;
    }
}
