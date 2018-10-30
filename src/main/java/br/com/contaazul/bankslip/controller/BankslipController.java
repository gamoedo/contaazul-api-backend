package br.com.contaazul.bankslip.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.contaazul.bankslip.controller.request.BankslipPaymentRequest;
import br.com.contaazul.bankslip.controller.request.BankslipRequest;
import br.com.contaazul.bankslip.controller.response.BankslipDetailResponse;
import br.com.contaazul.bankslip.controller.response.BankslipResponse;
import br.com.contaazul.bankslip.controller.response.BankslipResponseList;
import br.com.contaazul.bankslip.exception.UnprocessableEntityException;
import br.com.contaazul.bankslip.service.BankslipService;
import javassist.NotFoundException;

@RestController
public class BankslipController {

	@Autowired
	BankslipService bankslipService;

	@PostMapping(value = "/rest/bankslips/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public BankslipResponse createBankslip(@RequestBody @Valid BankslipRequest bankslipRequest)
			throws UnprocessableEntityException {
		return bankslipService.createBankslip(bankslipRequest);
	}

	@GetMapping(value = "/rest/bankslips/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BankslipResponseList listBankslips() {
		return bankslipService.listBankslips();
	}

	@GetMapping(value = "/rest/bankslips/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public BankslipDetailResponse detailsBankslip(@RequestBody @PathVariable(name = "id") String bankslipId) {
		return bankslipService.detailsBankslip(bankslipId);
	}

	@PostMapping(value = "/rest/bankslips/{id}/payments", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public BankslipResponse payBankslip(@RequestBody @Valid BankslipPaymentRequest bankslipPaymentRequest,
										@PathVariable(name = "id") String bankslipId) 
												throws NotFoundException {
		return bankslipService.payBankslip(bankslipId, bankslipPaymentRequest);
	}

	@DeleteMapping(value = "/rest/bankslips/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public BankslipResponse cancelBankslip(@RequestBody @PathVariable(name = "id") String bankslipId)
			throws NotFoundException {
		return bankslipService.cancelBankslip(bankslipId);
	}

}