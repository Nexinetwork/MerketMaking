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
	private String str6;
	private String str7;
	private String str8;
	private String str9;
	private String str10;

	private Long long1;
	private Long long2;
	private Long long3;
	private Long long4;
	private Long long5;
	private Long long6;
	private Long long7;
	private Long long8;
	private Long long9;
	private Long long10;

	private BigDecimal number1;
	private BigDecimal number2;
	private BigDecimal number3;
	private BigDecimal number4;
	private BigDecimal number5;
	private BigDecimal number6;
	private BigDecimal number7;
	private BigDecimal number8;
	private BigDecimal number9;
	private BigDecimal number10;

	private Integer int1;
	private Integer int2;
	private Integer int3;
	private Integer int4;
	private Integer int5;
	private Integer int6;
	private Integer int7;
	private Integer int8;
	private Integer int9;
	private Integer int10;

	private Boolean bool1;
	private Boolean bool2;
	private Boolean bool3;
	private Boolean bool4;
	private Boolean bool5;
	private Boolean bool6;
	private Boolean bool7;
	private Boolean bool8;
	private Boolean bool9;
	private Boolean bool10;

	private LocalDate date1;
	private LocalDate date2;
	private LocalDate date3;
	private LocalDate date4;
	private LocalDate date5;
	private LocalDate date6;
	private LocalDate date7;
	private LocalDate date8;
	private LocalDate date9;
	private LocalDate date10;

	private LocalDateTime dateTime1;
	private LocalDateTime dateTime2;
	private LocalDateTime dateTime3;
	private LocalDateTime dateTime4;
	private LocalDateTime dateTime5;
	private LocalDateTime dateTime6;
	private LocalDateTime dateTime7;
	private LocalDateTime dateTime8;
	private LocalDateTime dateTime9;
	private LocalDateTime dateTime10;

	private Object object1;
	private Object object2;
	private Object object3;
	private Object object4;
	private Object object5;
	private Object object6;
	private Object object7;
	private Object object8;
	private Object object9;
	private Object object10;

	private BigDecimal bigDecimal1;
	private BigDecimal bigDecimal2;
	private BigDecimal bigDecimal3;
	private BigDecimal bigDecimal4;
	private BigDecimal bigDecimal5;
	private BigDecimal bigDecimal6;
	private BigDecimal bigDecimal7;
	private BigDecimal bigDecimal8;
	private BigDecimal bigDecimal9;
	private BigDecimal bigDecimal10;

	private BigInteger bigInteger1;
	private BigInteger bigInteger2;
	private BigInteger bigInteger3;
	private BigInteger bigInteger4;
	private BigInteger bigInteger5;
	private BigInteger bigInteger6;
	private BigInteger bigInteger7;
	private BigInteger bigInteger8;
	private BigInteger bigInteger9;
	private BigInteger bigInteger10;

	private float flt1;
	private float flt2;
	private float flt3;
	private float flt4;
	private float flt5;
	private float flt6;
	private float flt7;
	private float flt8;
	private float flt9;
	private float flt10;

	private String blockchain;
	private String contractAddress;
	private String coin;
	private String coinSymbol;
	private String coingeckoId;
	private Long contractId;
	private Long blockchainId;
	private Long coinId;
	private Long walletId;
	private Long marketMakingId;
	private Long smartContractId;


}
