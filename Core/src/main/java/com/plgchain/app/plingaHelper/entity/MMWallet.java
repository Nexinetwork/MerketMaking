package com.plgchain.app.plingaHelper.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Document(collection = "marketmaking_wallet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MMWallet {

	@Id
	private long marketMakingId;

	@Indexed(unique = true)
	private long contractId;

	private long coinId;

	private long blockchainId;

	private String blockchain;

	private String coin;

	private String coinSymbol;

	private String contractAddress;

	private String transferWalletListGzip; // Field for storing compressed JSON data
	private String defiWalletListGzip; // Field for storing compressed JSON data

	private List<MarketMakingWalletDto> tankhahTransferWalletList;

    private List<MarketMakingWalletDto> tankhahDefiWalletList;

	@CreatedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastUpdateDate;

	public MMWallet(long marketMakingId, long contractId, long coinId, long blockchainId, String blockchain,
			String coin, String contractAddress) {
		this.marketMakingId = marketMakingId;
		this.contractId = contractId;
		this.coinId = coinId;
		this.blockchainId = blockchainId;
		this.blockchain = blockchain;
		this.coin = coin;
		this.contractAddress = contractAddress;
	}

	// Helper method to compress JSON data using GZIP
	private String compressToJson(List<MarketMakingWalletDto> dataList) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonData = objectMapper.writeValueAsString(dataList);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
			gzipOutputStream.write(jsonData.getBytes());
		}

		return byteArrayOutputStream.toString("ISO-8859-1");
	}

	public void setTransferWalletList(List<MarketMakingWalletDto> transferWalletList){
		try {
			this.transferWalletListGzip = compressToJson(transferWalletList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<MarketMakingWalletDto> decompressToMarketMakingWalletDtoList(String compressedData)
			throws IOException {
		byte[] compressedBytes = compressedData.getBytes("ISO-8859-1");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(compressedBytes);
		try (GZIPInputStream gzipInputStream = new GZIPInputStream(in)) {
			byte[] buffer = new byte[256];
			int n;
			while ((n = gzipInputStream.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		}

		String jsonData = out.toString();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonData,
				objectMapper.getTypeFactory().constructCollectionType(List.class, MarketMakingWalletDto.class));
	}

	public void setDefiWalletList(List<MarketMakingWalletDto> defiWalletList){
		try {
			this.defiWalletListGzip = compressToJson(defiWalletList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<MarketMakingWalletDto> getTransferWalletList(){
		try {
			return decompressToMarketMakingWalletDtoList(this.transferWalletListGzip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public List<MarketMakingWalletDto> getDefiWalletList() {
		try {
			return decompressToMarketMakingWalletDtoList(this.defiWalletListGzip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	// You can similarly create methods to decompress JSON data if needed

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MMWallet))
			return false;
		MMWallet other = (MMWallet) obj;
		return (contractId == other.contractId || marketMakingId == other.marketMakingId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contractId, marketMakingId);
	}
}
