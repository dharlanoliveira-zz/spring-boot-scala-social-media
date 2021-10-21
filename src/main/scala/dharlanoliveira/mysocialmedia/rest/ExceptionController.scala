package dharlanoliveira.mysocialmedia.rest

import dharlanoliveira.mysocialmedia.application.BusinessViolation
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ExceptionController {

  @ExceptionHandler(value = Array(classOf[BusinessViolation]))
  def handleBussinessViolation(ex: RuntimeException, request: WebRequest): ResponseEntity[String] = {
    val businessViolation = ex.asInstanceOf[BusinessViolation]
    new ResponseEntity[String](businessViolation.violation,HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(value = Array(classOf[RuntimeException]))
  def handleRuntimeException(ex: RuntimeException, request: WebRequest): ResponseEntity[String] = {
    ex.printStackTrace()
    new ResponseEntity[String](ex.getMessage, HttpStatus.INTERNAL_SERVER_ERROR)
  }

}
