package com.plgchain.app.plingaHelper.type.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GeneralReq implements Serializable {
	private static final long serialVersionUID = -6388484340153808578L;

	private String str1;

	private String str2;

	private String str3;

	private String str4;

	private String str5;

	private Long long1;

	private Long long2;

	private Long long3;

	private Long long4;

	private Long long5;

	private BigDecimal number1;

	private BigDecimal number2;

	private BigDecimal number3;

	private BigDecimal number4;

	private BigDecimal number5;

	private Integer int1;

	private Integer int2;

	private Integer int3;

	private Integer int4;

	private Integer int5;

	private Boolean bool1;

	private Boolean bool2;

	private Boolean bool3;

	private Boolean bool4;

	private Boolean bool5;

	private LocalDate date1;

	private LocalDate date2;

	private LocalDate date3;

	private LocalDate date4;

	private LocalDate date5;

	private LocalDateTime dateTime1;

	private LocalDateTime dateTime2;

	private LocalDateTime dateTime3;

	private LocalDateTime dateTime4;

	private LocalDateTime dateTime5;

	private Object object1;

	private Object object2;

	private Object object3;

	private Object object4;

	private Object object5;

	private BigDecimal bigDecimal1;

	private BigDecimal bigDecimal2;

	private BigDecimal bigDecimal3;

	private BigDecimal bigDecimal4;

	private BigDecimal bigDecimal5;

	private BigInteger bigInteger1;

	private BigInteger bigInteger2;

	private BigInteger bigInteger3;

	private BigInteger bigInteger4;

	private BigInteger bigInteger5;

}
