/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.types.expr;

import com.mysema.query.types.operation.Ops.Op;

/**
 * ExprFactory provides
 * 
 * @author tiwe
 * @version $Id$
 */
public interface ExprFactory {

	Expr<Integer> createConstant(int i);

	<A> Expr<Class<A>> createConstant(Class<A> obj);

	<A> Expr<A> createConstant(A obj);

	EBoolean createBoolean(Op<Boolean> operator, Expr<?>... args);

	<OpType, RT extends Comparable<?>> EComparable<RT> createComparable(Class<RT> type, Op<OpType> operator, Expr<?>... args);

	<OpType extends Number, D extends Number & Comparable<?>> ENumber<D> createNumber(Class<? extends D> type, Op<OpType> operator, Expr<?>... args);

	EString createString(Op<String> operator, Expr<?>... args);

	Expr<String[]> createStringArray(Op<String> operator, Expr<?>... args);

}
