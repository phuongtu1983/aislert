<%-- 
    Document   : config
    Created on : May 3, 2018, 10:16:37 AM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import ="java.util.Properties"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Configure parameters</title>
    </head>
    <body>
        <form action="/aisalert/testalert.do" method="POST">
            <table style="border-width: 0px">
                <tr><td><input type="submit" value="Test"/></td></tr>
            </table>
            <input type="hidden" name="tested" value="1"/>
        </form>
    </body>
</html>
