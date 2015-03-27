<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><fmt:message key="common.title" /></title>
<style>
.error {
	color: red;
}
</style>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

	<div class="panel panel-primary group category">
		<div class="panel-heading">
			<h3 class="panel-title list">			
			<span class="glyphicon glyphicon-edit" aria-hidden="true">&nbsp;</span>
			<fmt:message key="activity.mod" /></h3>
		</div>
		<div class="panel-body">
			<%-- 	<form:form  method="post" modelAttribute="modifyProduct" > (ResquestParam)  --%>

			<form:form method="post"  action="modify.htm" commandName="modifyactivity">
				<div class="form-group">
					<label><fmt:message key="input.code" />: </label>
					<form:input path="info.code" class="form-control" required="true" />
					<form:errors path="info.code" cssStyle="color: #ff0000" />
				</div>
				<div class="form-group">
					<label><fmt:message key="input.name" />: </label>
					<form:input path="info.name" class="form-control" id="name"
						required="true" />
					<form:errors path="info.name" cssStyle="color: #ff0000" />
				</div>
				<div class="form-group">
					<label><fmt:message key="input.desc" />: </label>
					<form:input class="form-control" path="info.description"
						id="description" required="true" />
					<form:errors path="info.description" cssStyle="color: #ff0000" />
				</div>

								

				<div class="panel-body">
					<label><fmt:message key="learninggoalstatus.list" /></label>
				
					<table class="table table-condensed">
						<tr align="center">
							<td width="20%"><div class="td-label"><fmt:message key="competence.com" /></div></td>
							<td width="50%"><div class="td-label"><fmt:message key="input.weight" /></div></td>
						</tr>
						
						<c:forEach items="${learningGoalStatus}" var="learnStatus" varStatus="status">		
							<tr align="center">
								<td><div class="td-content">
										<c:out value="${learnStatus.learningGoal.info.name}" />
								
									</div></td>
								<td>
									<div class="td-content">
										<c:out value="${learnStatus.weight}" />
										
									</div>
								</td>
								<td>
									<a href="<c:url value='competenceStatus/${learnStatus.learningGoal.id}/delete.htm'/>">
										
										<button type="button" class="btn btn-default btn-lg" 
										style=" padding: 2px; margin-top: 1.2%;  background: rgb(236, 236, 236);">
  										<span class="glyphicon glyphicon-remove" aria-hidden="true" ></span> 
										</button>
									</a>
							</td>
								
							</tr>
							
						</c:forEach>
						
						


					</table>
				</div>
				<spring:message code="activity.mod" var="acMod"/>
				<input type="submit" class="btn btn-primary btn-lg addActivity" value="${acMod}" />

			</form:form >
			<div class="addLearningStatus">
			<form:form method="post" action="addLearningStatus.htm" commandName="addlearningstatus">
					<h4 style=" color: forestgreen; text-decoration: underline; cursor: default;">   		
					<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
					<fmt:message key="activity.newLGS" /></h4>
					<br>
					<label><fmt:message key="competence.com" />:</label>
					<form:select class="form-control 2" path="learningGoal"
						id="learningGoal">
						<form:option value=""> --<fmt:message key="common.selectOp" />-- </form:option>
						<c:forEach items="${learningGoals}" var="learning">
							<form:option value="${learning.id}">${learning.info.name}</form:option>
						</c:forEach>
					</form:select>
					<br>
					<label><fmt:message key="input.weight" />:</label>
					<form:input class="form-control" path="weight"  id="weight"
						required="true" />
					<spring:message code="learninggoalstatus.add" var="lgsAdd"/>
					 <input type="submit" class="btn btn-success CompSta" name="button1"
						style="  cursor: copy;" value="${lgsAdd}" /> 

			</form:form>
			</div>
		</div>
	</div>
	
			<c:if test="${not empty errors}">
	<div align="center">
		<h3 class="panel-title list">	<fmt:message key="error.errors" />: </h3>	
			<br/>
			<c:forEach items="${errors}" var="error">
				<c:out  value="${error}" /><br/>
			</c:forEach>
	</div>
	</c:if>
</body>
</html>
