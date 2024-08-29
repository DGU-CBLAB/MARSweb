<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>VennDiagram</title>
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
						<h1 class="ftco-heading mb-3" data-aos="fade-up" data-aos-delay="500">VennDiagram</h1>
            					<h2 class="h5 ftco-subheading mb-5" data-aos="fade-up"  data-aos-delay="600"> </h2>    
	          			</div>
				</div>
      		</div>
		</section>


		<br><br>
		<div class="container" style="background:#f5f5f5;">
			<div class="col-md-7">
				<h1 data-aos="fade-up" data-aos-delay="500">Upload files to use VennDiagram</h1> <!--  class="ftco-heading mb-3"  -->
        			<h2 data-aos="fade-up"  data-aos-delay="600">For using this, you need to upload MARSweb result file.</h2>    <!-- class="h5 ftco-subheading mb-5" -->
			</div>

			<footer class="ftco-footer ftco-bg-dark ftco-section">
				<div class="ftco-section contact-section">
					<div class="container">
						<div class="row block-9">
							<div class="col-md-6 pr-md-5" style="background-image: none;">
								<input id="tab1" type="radio" name="tabs" value="type1"
								checked="checked" style="display: none"> <label
								for="tab1" data-tab="tab-1" style="padding: 15px 18px">MARSweb</label> 
							
							<div id="tab-1" class="tab-content current">
								<form name='venn' action="VennServlet" method="post" enctype="multipart/form-data">
									<div class="form-group">
										<input type="email" class="form-control" name="emailAddress" placeholder="The email address to receive the results.">
              						</div>
              						<div class="form-group">
              								<table  cellspacing="100%">
              									<tr>
              										<th cellspacing="50%"><input type="text" placeholder=" Upload MARSweb result" disabled></th>
              										<th><input type="file" name="SNP_pos"></th>
              									</tr>
              								</table>

              								<table  cellspacing="100%">
              									<tr>
              										<th cellspacing="50%"><input type="text" placeholder=" P-value Threshold" disabled></th>
              										<th><input type="number" name="threshold" min="0" max="1" step="0.001" style="width:101.88px" value="0.05"></th>
              									</tr>
              								</table>
            						
                							</div>	
                					<div class="form-group">
										<input type="submit" name="upload" value="Submit"
											class="btn btn-primary py-3 px-5">
									</div>
                					<input type="text" name="tabss" value="type1"
										style="display: none">
                								
								</form>
						</div>					
						</div>
							<div class="col-md-6 pr-md-5" style="background-image: none;">
						<label for="tab1" data-tab="tab-1" style="padding: 15px 18px; width:100%;pointer-events: none; color:black;">Data description</label>
						<div id="tab-1-1" class="tab-content current" style="background-image: none;height:284px; width:480px; overflow:auto; white-space:nowrap;"> 
						
					  	<p><font color="black" size=5>--------- MARSweb result --------- </font><br>
					  	<font color="black">
					  	There is three fileds per set <br>
					  	setname LRT_pvalue UNI_pvalue <br>
					  	ex) 3 set result <br>
					  	setname LRT_pvalue UNI_pvalue<br>
					  	set1 &nbsp;&nbsp; 0.01&nbsp;&nbsp;0.1<br>
					  	set2 &nbsp;&nbsp; 0.05&nbsp;&nbsp;0.2<br>
					  	set3 &nbsp;&nbsp; 0.2&nbsp;&nbsp; 0.9<br><br>
					  	</font>					  	
					  	</p>
						</div></div>	
					</div>
				</div>
					</div>
				</div>
			</footer>
		</div>
			<script>
		//tab-menu
		$(document).ready(function() {
			$('label').click(function() {
				var tab_id = $(this).attr('data-tab');
				$('.tab-content').removeClass('current');
				$(this).addClass('current');
				$("#" + tab_id).addClass('current');
				$("#" + tab_id + "-1").addClass('current');
			})
		})	</script>
  	</body>
</html>