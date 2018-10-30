package br.com.contaazul.bankslip.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import br.com.contaazul.bankslip.controller.request.BankslipPaymentRequest;
import br.com.contaazul.bankslip.controller.request.BankslipRequest;
import br.com.contaazul.bankslip.entity.Bankslip;
import br.com.contaazul.bankslip.entity.EnumStatus;
import br.com.contaazul.bankslip.exception.UnprocessableEntityException;
import br.com.contaazul.bankslip.helper.JsonHelper;
import br.com.contaazul.bankslip.service.BankslipService;
import br.com.contaazul.bankslip.util.Utils;
import javassist.NotFoundException;

@RunWith(SpringRunner.class)
@WebMvcTest(BankslipController.class)
@EnableSpringDataWebSupport
public class BankslipControllerUnitTest {

	@MockBean
	private BankslipService bankslipService;
	
	@Autowired
    private MockMvc mvc;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Bankslip bankslip;
	
	@Before
	public void setUp() throws UnprocessableEntityException, NotFoundException {
		bankslip = new Bankslip();
		bankslip.setId("84e8adbf-1a14-403b-ad73-d78ae19b59bf");
		bankslip.setCustomer("Ford Prefect Company");		
		bankslip.setDueDate(Utils.convertStringToLocalDate("2018-05-10", "yyyy-MM-dd"));
		bankslip.setTotalInCents(BigDecimal.valueOf(99000));
		bankslip.setStatus(EnumStatus.PENDING);
		bankslip.setPaymentDate(null);
		
		doReturn(bankslip).when(bankslipService).createBankslip(any(BankslipRequest.class));
		doReturn(Arrays.asList(bankslip)).when(bankslipService).listBankslips();
		doReturn(bankslip).when(bankslipService).detailsBankslip(eq("84e8adbf-1a14-403b-ad73-d78ae19b59bf"));						

		doThrow(new NotFoundException("")).when(bankslipService).detailsBankslip(eq("84e8adbf-1a14-403b-ad73-d78ae19b59bf"));
		doThrow(new NotFoundException("")).when(bankslipService).payBankslip(eq("84e8adbf-1a14-403b-ad73-d78ae19b59bf"),(any(BankslipPaymentRequest.class)));
	}
	
	@Test
	public void shouldCreateBankslip() throws Exception {
		String request = JsonHelper.getRequestFileAsString("bankslip/create_bankslip_request.json");
		String response = JsonHelper.getResponseFileAsString("bankslip/create_bankslip_response_success.json");

		mvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(request)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isCreated())
				.andExpect(content().json(response));

		verify(bankslipService, times(1)).createBankslip(any(BankslipRequest.class));
	}
	
	@Test
	public void shouldListBankslip() throws Exception {		

		String response = JsonHelper.getResponseFileAsString("bankslip/create_bankslip_response_success.json");
		
		mvc.perform(get("/rest/bankslips/")
				 .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().json(response));

		verify(bankslipService, times(1)).listBankslips();
	}
	
	@Test
	public void shouldPayBankslip() throws Exception {
		
		String request = JsonHelper.getRequestFileAsString("bankslip/create_bankslip_payment_request.json");
		
		bankslip.setStatus(EnumStatus.PAID);
		bankslip.setPaymentDate(Utils.convertStringToLocalDate("2018-05-15", "yyyy-MM-dd"));

		mvc.perform(post("/rest/bankslips/84e8adbf-1a14-403b-ad73-d78ae19b59bf/payments")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(request))
				.andExpect(status().isNoContent());

		verify(bankslipService, times(1)).payBankslip(eq("84e8adbf-1a14-403b-ad73-d78ae19b59bf"), (any(BankslipPaymentRequest.class)));
	}
	
	@Test
	public void shouldCancelBankslip() throws Exception {
		mvc.perform(delete("/rest/bankslips/84e8adbf-1a14-403b-ad73-d78ae19b59bf"))
				.andExpect(status().isNoContent());

		verify(bankslipService, times(1)).cancelBankslip("84e8adbf-1a14-403b-ad73-d78ae19b59bf");
	}
	
}