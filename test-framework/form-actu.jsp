<%@page import="formulaire.Formulaire" %>
<%

    Formulaire form = (Formulaire) request.getAttribute("formulaire");

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./assets/css/bootstrap.css">
    <title>Actualite</title>
</head>
<body>
    <div class="container mt-5">
        <%=form.getHTMLString() %>
    </div>
</body>
</html>