<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./assets/css/bootstrap.css">
    <title>Helloworld</title>
</head>
<body>
    <form action="upload" method="post" enctype="multipart/form-data">
        <input type="text" name="id" placeholder="Id" class="form-control">
        <input type="text" name="name" placeholder="Name" class="form-control">
        <input type = "file" name = "image" class="form-control">
        <input type="submit" value="Valider" class="btn btn-dark">
    </form>
</body>
</html>