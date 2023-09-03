<%@page import="model.actualite.Actualite" %>
<%
    
    Actualite[] actualites = (Actualite[]) request.getAttribute("actualites");

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
    <h1 class="text-center mt-5">Actualite</h1>
    <table class="table mt-5 w-75 mx-auto">
        <tr>
            <th>Image</th>
            <th>Titre</th>
            <th>Categorie</th>
            <th>Date</th>
        </tr>
        <% for (Actualite actualite : actualites) { %>
        <tr>
            <td><img src="./assets/img/<%=actualite.getPhoto() %>"></td>
            <td><%=actualite.getTitre() %></td>
            <td><%=actualite.getCategorie().getNom() %></td>
            <td><%=actualite.getDate() %></td>
        </tr>
        <% } %>
    </table>
</body>
</html>