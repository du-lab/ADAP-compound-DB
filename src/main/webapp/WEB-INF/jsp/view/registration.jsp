<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

<!-- Start the middle column -->

<section>
    <h1>User Registration</h1>
    <div align="center">
        <form method="POST" action="<c:url value="/registration"/>">
            <div style="display: inline-block; width: 40%;">
                <h2>Login / Password</h2>
                <label>
                    <span>Username</span>
                    <input type="text" name="username"/>
                </label>
                <label>
                    <span>New password</span>
                    <input type="text" name="password"/>
                </label>
                <label>
                    <span>Repeat password</span>
                    <input type="text" name="password2"/>
                </label>

                <h2>Information</h2>
                <label>
                    <span>First Name</span>
                    <input type="text" name="firstName"/>
                </label>
                <label>
                    <span>Last Name</span>
                    <input type="text" name="secondName"/>
                </label>
                <label>
                    <span>E-mail</span>
                    <input type="text" name="email"/>
                </label>
            </div>

            <div style="display: inline-block; width: 40%;">
                <h2>Organization</h2>
                <label>
                    <span>Name of Organization</span>
                    <input type="text" name="companyName"/>
                </label>
                <label>
                    <span>Address Line 1</span>
                    <input type="text" name="address1"/>
                </label>
                <label>
                    <span>Address Line 2</span>
                    <input type="text" name="address2"/>
                </label>
                <label>
                    <span>City</span>
                    <input type="text" name="city"/>
                </label>
                <label>
                    <span>State</span>
                    <input type="text" name="state"/>
                </label>
                <label>
                    <span>ZIP code</span>
                    <input type="text" name="zip"/>
                </label>
                <label>
                    <span>Country</span>
                    <input type="text" name="country"/>
                </label>
            </div>
            <br/>
            <label>
                <span>&nbsp;</span>
                <input type="submit" value="Register"/>
            </label>
        </form>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />