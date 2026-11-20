package tn.itbs.analyse.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.analyse.calcul.DonneesInsuffisantesException;

// Toutes les erreurs sont renvoyées au même format JSON : {timestamp, status, erreur, details?}
@RestControllerAdvice
public class GlobalExceptionHandler {

	// Erreurs métier levées dans les services (404, 400, 409...)
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> erreurMetier(ResponseStatusException e) {
		return reponse(e.getStatusCode(), e.getReason(), null);
	}

	// Le modèle demandé ne peut pas être appliqué avec les données fournies (422)
	@ExceptionHandler(DonneesInsuffisantesException.class)
	public ResponseEntity<Map<String, Object>> donneesInsuffisantes(DonneesInsuffisantesException e) {
		return reponse(HttpStatusCode.valueOf(422), e.getMessage(), null);
	}

	// @Valid : un ou plusieurs champs invalides
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> champsInvalides(MethodArgumentNotValidException e) {
		Map<String, String> details = new LinkedHashMap<>();
		for (FieldError fe : e.getBindingResult().getFieldErrors()) {
			details.put(fe.getField(), fe.getDefaultMessage());
		}
		return reponse(HttpStatus.BAD_REQUEST, "Données invalides", details);
	}

	// JSON mal formé ou valeur d'enum inconnue (ex. culture "BANANE")
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> corpsIllisible(HttpMessageNotReadableException e) {
		return reponse(HttpStatus.BAD_REQUEST, "Corps de la requête invalide ou mal formé", null);
	}

	// Paramètre d'URL du mauvais type (ex. /parcelles/abc)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> parametreInvalide(MethodArgumentTypeMismatchException e) {
		return reponse(HttpStatus.BAD_REQUEST, "Paramètre '" + e.getName() + "' invalide", null);
	}

	private ResponseEntity<Map<String, Object>> reponse(HttpStatusCode status, String message, Object details) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", status.value());
		body.put("erreur", message);
		if (details != null) {
			body.put("details", details);
		}
		return ResponseEntity.status(status).body(body);
	}
}
