<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<title>Login page</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    
		<link href="https://fonts.googleapis.com/css?family=Rubik:300,400,500" rel="stylesheet">
		<link rel="stylesheet" href="css/open-iconic-bootstrap.min.css">
		<link rel="stylesheet" href="css/animate.css">
		<link rel="stylesheet" href="css/owl.carousel.min.css">
		<link rel="stylesheet" href="css/owl.theme.default.min.css">
		<link rel="stylesheet" href="css/magnific-popup.css">
		<link rel="stylesheet" href="css/aos.css">
		<link rel="stylesheet" href="css/ionicons.min.css">
		<link rel="stylesheet" href="css/bootstrap-datepicker.css">
		<link rel="stylesheet" href="css/jquery.timepicker.css">
		<link rel="stylesheet" href="css/flaticon.css">
		<link rel="stylesheet" href="css/icomoon.css">
		<link rel="stylesheet" href="css/style.css">
		<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
		<script type="text/javascript"> 

		function loginchk(){
			 var regx = /^[a-zA-Z0-9]*$/;
			 var id = document.form.id.value;
			 var pass = document.form.pass.value;
			 if (id.length == 0 || id == null) {
			  alert("아이디를 입력하시오");
			  document.form.id.focus();
			  return;
			 }
			 if (!regx.test(id)){
			  alert("아이디는 영어, 숫자만 입력가능합니다.");
			  document.form.id.focus();
			  return false;
			 }
		 if (pass.length < 6 || pass == null) {
		  alert("비밀번호는 6글자이상입니다");
		  document.form.pass.focus();
		  return;
		 }
		 document.form.submit();
		}
		</script>
	</head>
	<body>
		<nav class="navbar navbar-expand-lg navbar-dark ftco_navbar bg-dark ftco-navbar-light" id="ftco-navbar" data-aos="fade-down" data-aos-delay="500">
			<div class="container">
				<a class="navbar-brand" href="index.html">CBLAB - Computational Bioinformatics LABoratory</a>
				<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#ftco-nav" aria-controls="ftco-nav" aria-expanded="false" aria-label="Toggle navigation">
				<span class="oi oi-menu"></span> Menu
        			</button>

        			<div class="collapse navbar-collapse" id="ftco-nav">
          				<ul class="navbar-nav ml-auto">
            				<li class="nav-item active"><a href="index.html" class="nav-link">Home</a></li>
            				<li class="nav-item"><a href="GAMMA_index.jsp" class="nav-link">GAMMA</a></li>
            				<li class="nav-item"><a href="MultiTrans_index.jsp" class="nav-link">MultiTrans</a></li>
            				<li class="nav-item"><a href="NICE_index.jsp" class="nav-link">NICE</a></li>
            				<li class="nav-item"><a href="eQTL_index.jsp" class="nav-link">eQTL</a></li>
            				<li class="nav-item"><a href="Manhattan_index.jsp" class="nav-link">Manhattan</a></li>
            				<li class="nav-item"><a href="Signup.html" class="nav-link">SignUp</a></li>
							<li class="nav-item"><a href="loginForm.jsp" class="nav-link">SignIn</a></li>
							<li class="nav-item"><a href="logoutProc.jsp" class="nav-link">SignOut</a></li>
          				</ul>
        			</div>
      		</div>
		</nav>


		<section class="ftco-cover" style="background-image: url(images/genes_650x400_61470411347.jpg); max-width:auto; max-height:auto;" id="section-home" data-aos="fade"  data-stellar-background-ratio="0.5">
			<div class="container">
				<div class="row align-items-center ftco-vh-75">
					<div class="col-md-7">
						<h1 class="ftco-heading mb-3" data-aos="fade-up" data-aos-delay="500">Need LogIn</h1>
            			</div>
				</div>
      		</div>
		</section>


		<br><br>
		<div class="container">
			<div class="col-md-7">
				<h1 data-aos="fade-up" data-aos-delay="500">Compbiojoo</h1> <!--  class="ftco-heading mb-3"  -->
        			<h2 data-aos="fade-up"  data-aos-delay="600">For analysis your data</h2>    <!-- class="h5 ftco-subheading mb-5" -->
			</div>

			<footer class="ftco-footer ftco-bg-dark ftco-section">
				<div class="ftco-section contact-section">
					<div class="container">
						<div class="row block-9">
							<div class="col-md-6 pr-md-5">
								<form action="loginProc.jsp" method="post" name="form">
            							<div class="form-group">
              								<table  cellspacing="100%">
              									<tr>
              										<th cellspacing="50%"><input type="text" placeholder=" Input your Id" name = "id"></th>
              									</tr>
              									<tr>
              	        							<th cellspacing="50%"><input type="password" placeholder=" Password" name = "pass"></th>
      											</tr>
              								</table>
            								</div>
											<div class="form-group">
                								<input type="button" value="Log In" class="btn btn-primary py-3 px-5" onclick="loginchk()"> </button>
            								</div>
        						</div>
								</form>
							<div class="col-md-6" style="background-image: url(images/dongkuk_560.jpg); width:560px; height:448px;"> </div>
      					</div>
					</div>
				</div>
      			<div class="row">
        				<div class="col-md-12 text-center">
          					<p>
							Dongguk University, Seoul, Korea
          					</p>
        				</div>
      			</div>
			</footer>
		</div>

	<script src="js/jquery.min.js"></script>
  	<script src="js/jquery-migrate-3.0.1.min.js"></script>
  	<script src="js/popper.min.js"></script>
  	<script src="js/bootstrap.min.js"></script>
  	<script src="js/jquery.easing.1.3.js"></script>
  	<script src="js/jquery.waypoints.min.js"></script>
  	<script src="js/jquery.stellar.min.js"></script>
  	<script src="js/owl.carousel.min.js"></script>
  	<script src="js/jquery.magnific-popup.min.js"></script>
  	<script src="js/aos.js"></script>
  	<script src="js/jquery.animateNumber.min.js"></script>
  	<script src="js/google-map.js"></script>
  	<script src="js/main.js"></script>

  	</body>
</html>
