/*
 * @(#)$Id: IsSetField.java,v 1.3 2005-09-08 23:29:22 kohsuke Exp $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class IsSetField extends AbstractField {

    private final FieldOutline core;

    private final boolean generateUnSetMethod;
    private final boolean generateIsSetMethod;

    protected IsSetField( ClassOutlineImpl outline, CPropertyInfo prop,
            FieldOutline core, boolean unsetMethod, boolean issetMethod ) {
        super(outline,prop);
        this.core = core;
        this.generateIsSetMethod = issetMethod;
        this.generateUnSetMethod = unsetMethod;
        
        generate(outline,prop);
    }
    
    
    private void generate( ClassOutlineImpl outline, CPropertyInfo prop ) {
        // add isSetXXX and unsetXXX.
        MethodWriter writer = outline.createMethodWriter();
        
        JCodeModel codeModel = outline.parent().getCodeModel();
        
        FieldAccessor acc = core.create(JExpr._this());
        
        if( generateIsSetMethod ) {
            // [RESULT] boolean isSetXXX()
            JExpression hasSetValue = acc.hasSetValue();
            if( hasSetValue==null ) {
                // this field renderer doesn't support the isSet/unset methods generation.
                // issue an error
                throw new UnsupportedOperationException();
            }
            writer.declareMethod(codeModel.BOOLEAN,"isSet"+this.prop.getName(true))
                .body()._return( hasSetValue );
        }
        
        if( generateUnSetMethod ) {
            // [RESULT] void unsetXXX()
            acc.unsetValues(
                writer.declareMethod(codeModel.VOID,"unset"+this.prop.getName(true)).body() );
        }
    }


    public JBlock getOnSetEventHandler() {
        return core.getOnSetEventHandler();
    }

    public JType getRawType() {
        return core.getRawType();
    }
    
    public FieldAccessor create(JExpression targetObject) {
        return new Accessor(targetObject);
    }
    
    private class Accessor extends AbstractField.Accessor {
        
        private final FieldAccessor core;
        
        Accessor( JExpression $target ) {
            super($target);
            this.core = IsSetField.this.core.create($target);
        }
        

        public void unsetValues( JBlock body ) {
            core.unsetValues(body);
        }
        public void toArray( JBlock block, JExpression $array ) {
            core.toArray(block,$array);
        }
        public JExpression hasSetValue() {
            return core.hasSetValue();
        }
        public JExpression getContentValue() {
            return core.getContentValue();
        }

        public void add(JBlock body, JExpression newValue) {
            core.add(body,newValue);
        }

        public void toRawValue(JBlock block, JVar $var) {
            core.toRawValue(block,$var);
        }

        public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
            core.fromRawValue(block,uniqueName,$var);
        }
    }
}
