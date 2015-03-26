<%@ include file="/WEB-INF/views/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><fmt:message key="title" /></title>
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
				<span class="glyphicon glyphicon-plus" aria-hidden="true">&nbsp;</span>
				<fmt:message key="${typeform}"/> <fmt:message key="mod" />
			</h3>
		</div>
		<div class="panel-body">
			<form:form method="post" commandName="module" role="form">

				<form:hidden path="id" />

		<spring:message code="modCode" var="modCode"/>
		<spring:message code="modName" var="modName"/>
		<spring:message code="modDesc" var="modDesc"/>
		
				<div class="form-group">
					<label><fmt:message key="code" />: </label>
					<form:input path="info.code" class="form-control"
						placeholder="${modCode}" required="true" />
					<form:errors path="info.code" cssStyle="color: #ff0000" />

				</div>
				<div class="form-group">
					<label><fmt:message key="name" />: </label>
					<form:input path="info.name" class="form-control"
						placeholder="${modName}" required="true" />
					<form:errors path="info.name" cssStyle="color: #ff0000" />

				</div>
				<div class="form-group">
					<label><fmt:message key="desc" />: </label>
					<form:input path="info.description" class="form-control"
						placeholder="${modDesc}" required="true" />
					<form:errors path="info.description" cssStyle="color: #ff0000" />

				</div>
		<spring:message code="undelete" var="undelete"/>

				<input type="submit" class="btn btn-success" value="<fmt:message key="${typeform}"/>"
					name="${valueButton}" />
				<c:if test="${unDelete == true}">
					<input type="submit" class="btn btn-success" value="${undelete}"
						name="Undelete" />
				</c:if>
			</form:form>
		</div>
	</div>
	<c:if test="${not empty errors}">
		<div align="center">
			<h3 class="panel-title list"><fmt:message key="errors" />:</h3>
			<br />
			<c:forEach items="${errors}" var="error">
				<c:out value="${error}" />
				<br />
			</c:forEach>
		</div>
	</c:if>
</body>
</html>
