/**
 *
 */
package com.plgchain.app.general;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.plgchain.app.plingaHelper.util.ArrayListHelper;
import com.plgchain.app.plingaHelper.util.NumberUtil;

/**
 *
 */
public class NumberUtilTest implements Serializable {

	private static final long serialVersionUID = 398249001380270381L;

	//@Test
	public void generateRandomTestCase() {
		System.out.println("Random is : " + NumberUtil.generateRandomNumber(new BigDecimal(10), new BigDecimal(2000), 0));
	}

	@Test
	public void testChunkTestCase() {
		List<Integer> myList = new ArrayList<>();
        for (int i = 1; i <= 11251; i++) {
            myList.add(i);
        }

        int chunkSize = 10;
        List<List<Integer>> chunkedLists = ArrayListHelper.chunkList(myList, chunkSize);

        for (List<Integer> chunk : chunkedLists) {
            System.out.println(chunk);
        }
	}

}
