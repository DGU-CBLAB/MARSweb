<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>GAMMA</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<!-- Include fonts -->
		<%@ include file="module/fonts.jsp" %>
		<!-- Include styles-->
		<%@ include file="module/styles.jsp" %>
	</head>
<body style="background:#fff;">
	<!-- Include nav bar -->
	<%@ include file="module/nav.jsp" %>
	<!-- Include java script -->
	<%@ include file="module/js_script.jsp" %>


	<section class="ftco-cover"
		style="background-image: url(images/mars_wallpaper3.jpg); background-size: cover; background-repeat:no-repeat;"
		id="section-home" data-aos="fade" data-stellar-background-ratio="0.5">
			<div class="container">
				<div class="row align-items-center ftco-vh-75">
					<div class="col-md-7">
						<h1 class="ftco-heading mb-3" data-aos="fade-up" data-aos-delay="500">GAMMA</h1>
						<h2 class="h5 ftco-subheading mb-5" data-aos="fade-up"  data-aos-delay="600">Generalized Analysis of Molecular variance for Mixed model Analysis <a href="http://genetics.cs.ucla.edu/GAMMA/" target="_blank">GAMMA</a> is a method that can simultaneously analyze many phenotypes as well as correct for population structure. (Click the bold text if you want to use the program manually.)</h2>    
	          			</div>
				</div>
      		</div>
		</section>


		<br><br>
		<div style="background:#f5f5f5;" class="container">
			<div class="col-md-7">
				<h1 data-aos="fade-up" data-aos-delay="500">Upload files to use GAMMA</h1> <!--  class="ftco-heading mb-3"  -->
				<h2 data-aos="fade-up"  data-aos-delay="600">For analysis using GAMMA, you need to upload SNP file, phenotype file and input the number of SNPs.</h2>    <!-- class="h5 ftco-subheading mb-5" -->
			</div>

			<footer class="ftco-footer ftco-bg-dark ftco-section">
				<div class="ftco-section contact-section">
					<div class="container">
						<div class="row block-9">
							<div class="col-md-6 pr-md-5">
							<div id="tab-1" class="tab-content current" style="background:#f5f5f5; padding:10px; height:300px;">
								<form action="GAMMAServlet" method="post" enctype="multipart/form-data">
									<div class="form-group">
										<input type="email" class="form-control" name = "emailAddress" placeholder="The email address to receive the results.">
									</div>
            								<div class="form-group">
	              								<table  cellspacing="100%">
	              									<tr>
	              										<th cellspacing="50%"><input type="text" placeholder=" Upload SNP file" disabled></th>
	              										<th><input type="file" name="SNPfile"></th>
	              									</tr>
	              									<tr>
	              										<th cellspacing="50%"><input type="text" name="NumSNPs" placeholder=" The number of SNPs"></th>
	              									</tr>	
	              								</table>
            								</div>
									<div class="form-group">
              								<table  cellspacing="100%">
              									<tr>
              										<th cellspacing="50%"><input type="text" placeholder=" Upload Phenotype file" disabled></th>
              										<th><input type="file" name="Phenotypefile" ></th>
              									</tr>
              								</table>
            								</div>
            								<div class="form-group" style="float:right;">
                								<input type="submit" name="upload" value="Submit" class="btn btn-primary py-3 px-5">
            								</div>
        							</div>
        							</div>>
        						</form>
								<div class="col-bg"> <img style="float:left; width:0%;"src="images/word_cloud.png" ></div>
      					</div>
					</div>
				</div>
      			<div class="row">
        				<div class="col-md-12 text-center">
          					<p>
          					</p>
        				</div>
      			</div>
			</footer>
		</div>
  	</body>
</html>