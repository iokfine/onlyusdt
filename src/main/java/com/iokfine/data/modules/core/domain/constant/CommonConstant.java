package com.iokfine.data.modules.core.domain.constant;


/**
 * Created by void on 2018/7/2.
 * 通用类型
 */
public interface CommonConstant {

    enum YesOrNO{
        YES(1),
        NO(0);
        private Integer code;

        YesOrNO(Integer code) {
            this.code = code;
        }

        public Integer val(){
            return code;
        }
    }


    enum ON_OFF{
        Off(1),
        On(0);
        private Integer code;

        ON_OFF(Integer code){
            this.code = code;
        }

        public Integer val() {
            return code;
        }
    }
}
