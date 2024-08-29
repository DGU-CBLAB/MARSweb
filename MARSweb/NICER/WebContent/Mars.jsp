<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Mars</title>
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
					<h1 class="ftco-heading mb-3" data-aos="fade-up"
						data-aos-delay="500">MARSweb</h1><br>
					<h2 class="h5 ftco-subheading mb-5" data-aos="fade-up"
						data-aos-delay="600">
						MARSweb is model-based method tool<br> 
						that finds associations between variants <br>in risk loci and a phenotype, considering <br>the causal status of variants.<br><br>
					</h2>
				</div>
			</div>
		</div>
	</section>


	<br>
	<br>
	<div class="container" style="background:#f5f5f5;">
		<div class="col-md-7">
			<h1 data-aos="fade-up" data-aos-delay="500">Upload Input files
				for MARSweb</h1>
			<h2 data-aos="fade-up" data-aos-delay="600" style="width:800px;">You need a stat(Z-score) file and reference genome <br>&nbsp;</br></h2>
		</div>

		<footer class="ftco-footer ftco-bg-dark ftco-section">
			<div class="ftco-section contact-section">
				<div class="container">
					<div class="row block-9">
						<div class="col-md-6 pr-md-5" style="background-image: none;">
							<input id="tab1" type="radio" name="tabs" value="type1"
								checked="checked" style="display: none"> <label
								for="tab1" data-tab="tab-1" style="padding: 15px 18px">Genotype + Stat</label>
							<input id="tab2" type="radio" name="tabs" value="type2"
								style="display: none"> <label for="tab2"
								data-tab="tab-2" style="padding: 15px 18px">LD + Stat</label> 
							<input id="tab3" type="radio" name="tabs" value="type3"
								style="display: none"> <label for="tab3"
								data-tab="tab-3" style="padding: 15px 18px">Stat</label> 
								
							<div id="tab-1" class="tab-content current">
								<form action="MarsServlet" method="post"
									enctype="multipart/form-data">
									<div class="form-group">
										<input type="email" class="form-control" name="emailAddress"
											placeholder="The email address to receive the results.">
									<input type="text" name="tabss" value="type1"
										style="display: none">
										<table cellspacing="100%">
										    <tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Upload Set file" disabled ></th>
												<th>
												<input type="file" placeholder=" " name="set_file" accept=".tar.gz,.zip,.gz,.tar" ></th>																								
											</tr>
											<tr>
												<th cellspacing="50%"><input type="text"
												placeholder="Sampling Method" disabled></th>
												<th>
													<select name="Sampling_method" style="width:115px;height:35px;">
													<option value="0">Normal Sampling</option>
													<option value="1" selected>Fast Sampling</option>
													<option value="2">Importance Sampling</option>		
													</select>
												</th>
											</tr>
											<tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Number of Sampling" disabled></th>
												<th><input type="number" value="10000" name="sampling_number" min="1000", max="100000000"></th>
											</tr>
										</table>
									</div>
									<div class="form-group">
										<input type="submit" name="upload" value="Submit"
											class="btn btn-primary py-3 px-5">
									</div>
								</form>
							</div>
							
							<div id="tab-2" class="tab-content">
								<form action="MarsServlet" method="post"
									enctype="multipart/form-data">
									<div class="form-group">
										<input type="email" class="form-control" name="emailAddress"
											placeholder="The email address to receive the results.">
									<input type="text" name="tabss" value="type2"
										style="display: none">

										<table cellspacing="100%">
										    <tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Upload Set file" disabled ></th>
												<th>
												<input type="file" placeholder=" " name="set_file" accept=".tar.gz,.zip,.gz,.tar" ></th>																								
											</tr>
											<tr>
												<th cellspacing="50%"><input type="text"
												placeholder="Sampling Method" disabled></th>
												<th>
													<select name="Sampling_method" style="width:115px;height:35px;" disabled>
													<option value="0" selected>Normal Sampling</option>
													</select>
												</th>
											</tr>
											<tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Number of Sampling" disabled></th>
												<th><input type="number" value="10000" name="sampling_number" min="1000", max="100000000"></th>
											</tr>
										</table>
									</div>



									<div class="form-group">
										<input type="submit" name="upload" value="Submit"
											class="btn btn-primary py-3 px-5">
									</div>
								</form>
							</div>
							
							<div id="tab-3" class="tab-content">
								<form action="MarsServlet" method="post"
									enctype="multipart/form-data">
									<div class="form-group">
										<input type="email" class="form-control" name="emailAddress"
											placeholder="The email address to receive the results.">
									<input type="text" name="tabss" value="type3"
										style="display: none">
										<table cellspacing="100%">
										    <tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Upload your statistic file" disabled ></th>
												<th>
												<input type="file" placeholder=" " name="stat_file" ></th>																								
											</tr>
										    <tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Upload your Set info file" disabled ></th>
												<th>
												<input type="file" placeholder=" " name="set_info_file" ></th>																								
											</tr>
											<tr>
												<th cellspacing="50%"><input type="text"
													placeholder="tss around distance" disabled></th>
												<th><input type="number" value="10000" name="range_number" min="10000", max="100000000"></th>
											</tr>											
											<tr>
												<th cellspacing="50%"><input type="text"
												placeholder="Sampling Method" disabled></th>
												<th>
													<select name="Sampling_method" style="width:115px;height:35px;" disabled>
													<option value="0" selected>Normal Sampling</option>
													</select>
												</th>
											</tr>
											<tr>
												<th cellspacing="50%"><input type="text"
													placeholder="Number of Sampling" disabled></th>
												<th><input type="number" value="10000" name="sampling_number" min="1000", max="100000000"></th>
											</tr>
										</table>
									</div>
									<div class="form-group">
										<input type="submit" name="upload" value="Submit"
											class="btn btn-primary py-3 px-5">
									</div>
								</form>
							</div>
							
						</div>
						<div class="col-md-6 pr-md-5" style="background-image: none;">
						<label for="tab1" data-tab="tab-1" style="padding: 15px 18px; width:100%;pointer-events: none; color:black;">Data description</label>
						<div id="tab-1-1" class="tab-content current" style="background-image: none;height:284px; width:480px; overflow:auto; white-space:nowrap;"> 
						
					  	<p><font color="black" size=5>--------- Genotype Data --------- <br> sample_geno </font><br>
					  	<font color="black">
					  	There is two fileds per variants
					  	variants_id counted_allele allelic dosage (0, 1, 2, NA for diploid variants) <br>
					  	ex) 3 variants 5samples file <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;snp1 0 1 1 2 1 <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;snp2 1 1 2 1 2 <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;snp3 0 0 0 1 2 <br><br>
					  	</font>
					  	<font color="black" size=5>--------- Statistic Data --------- <br> sample_stat </font><br>
					  	<font color="black">
					  	The file in which Z-score are recorded according to the order of variables in the geno file <br>
					  	ex) 3 variants <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-0.547843<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-0.440051<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.451124<br><br>
					  	
					  	
					  	The names of the geno file and stat file of the set to be analyzed must be the same. <br>
						ex) A_geno, A_stat, B_geno, B_stat
					  	</font>
					  	<p>
						</div>
					  	<div id="tab-2-1" class="tab-content" style="background-image: none;height:284px; width:480px; overflow:auto; white-space:nowrap;">
					  						  	<p><font color="black" size=5>--------- LD Data --------- <br> sample_ld </font><br>
					  	<font color="black">
					  	The file containing a matrix representing the correlation between variants <br>
					  	ex) 3 variants file <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1 &nbsp;&nbsp; 0.3&nbsp;&nbsp;0.1<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.3&nbsp;&nbsp; 1 &nbsp;&nbsp; 0.2<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.1&nbsp;&nbsp;0.2&nbsp;&nbsp; 1<br><br>
					  	</font>
					  	<font color="black" size=5>--------- Statistic Data --------- <br> sample_stat </font><br>
					  	<font color="black">
					  	The file in which Z-score are recorded according to the order of variables in the ld file <br>
					  	ex) 3 variants <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-0.547843<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-0.440051<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.451124<br><br>
					  	
					  	
					  	The names of the ld file and stat file of the set to be analyzed must be the same. <br>
						ex) A_ld, A_stat, B_ld, B_stat
					  	</font>
					  	<p>
						</div>
					  	<div id="tab-3-1" class="tab-content" style="background-image: none; height:360px; width:480px; overflow:auto; white-space:nowrap;">
					  						  	<p><font color="black" size=5>--------- Statistic file --------- </font><br><br>
					  	<font color="black">
					  	Produced by association test on a quantitative phenotype using <a href="https://www.cog-genomics.org/plink/1.9/formats#qassoc">plink</a>(.qassoc). <br>
						The data columns required for analysis are CHR, SNP, BP, BETA, P. <br>
						Even in other types of summary statistic files, <br>
						there is no problem in analysis if only the values requested above are provided. <br>
						Fill other column values with 0. <br>
						<br>
					  	</font>
					  	<font color="black" size=5>--------- Set info Data --------- <br> </font><br>
					  	<font color="black">
					  	A text file with a header line, and then one line per set info with chromosome number(chr), set_name, set position <br>
					  	ex)&nbsp; 2 set info <br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chr&nbsp; set_name&nbsp; pos<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;11&nbsp;&nbsp;&nbsp; set1&nbsp;&nbsp;&nbsp; 200000<br>
					  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;21&nbsp;&nbsp;&nbsp; set2&nbsp;&nbsp; 10000000<br>
					  	</font>
					  	<p>
						</div></div>
			
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 text-center">
					<p></p>
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
		})
	</script>
</body>
</html>