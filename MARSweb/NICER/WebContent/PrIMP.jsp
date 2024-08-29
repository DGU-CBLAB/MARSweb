<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>PrIMP</title>
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
						data-aos-delay="500">PrIMP</h1>
					<h2 class="h5 ftco-subheading mb-5" data-aos="fade-up"
						data-aos-delay="600">
						<a
							href="https://github.com/bzlee-bio/PrIMP" target="_blank">PrIMP</a>
						 predicts modulability of 4 ion channels by accurate prediction models from only peptide sequence.
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
				for PrIMP</h1>
			<h2 data-aos="fade-up" data-aos-delay="600">You need fasta file </h2>

		</div>


		<footer class="ftco-footer ftco-bg-dark ftco-section">
			<div class="ftco-section contact-section">
				<div class="container">
					<div class="row block-9">
						<div class="col-md-6 pr-md-5" style="background-image: none;">
								<input id="tab1" type="radio" name="tabs" value="type1"
								checked="checked" style="display: none"> <label for="tab1"
								data-tab="tab-1" style="padding: 15px 18px">Fasta</label> 

							<div id="tab-1" class="tab-content current">
								<form action="PrIMPServlet" method="post"
									enctype="multipart/form-data">

									<div class="form-group">
										<input type="email" class="form-control" name="emailAddress"
											placeholder="The email address to receive the results.">
									</div>
									<input type="text" name="tabss" value="type1"
										style="display: none">
									<div class="form-group">
										<table cellspacing="100%">
											<tr>
												<th cellspacing="50%"><input type="text"
													placeholder=" Upload Fasta file" disabled ></th>
												<th><input type="file" name="Fasta_file"></th>
											</tr>
										</table>
									</div>
									<br>
									<br>
									<div class="form-group">
										<input type="submit" name="upload" value="Submit"
											class="btn btn-primary py-3 px-5">
									</div>
								</form>
							
							</div>
		
						</div>
					  	<div class="col-bg"> <img style="float:left; width:0%;"src="images/word_cloud.png" ></div>
				</div>
			</div>
	<div id="readme" class="Box md js-code-block-container Box--responsive">

      <div class="d-flex js-sticky js-position-sticky top-0 border-top-0 border-bottom p-2 flex-items-center flex-justify-between color-bg-primary rounded-top-2" style="position: sticky; z-index: 90; top: 0px !important;" data-original-top="0px">
        <div class="d-flex flex-items-center">
          

          <h2 class="Box-title">
				&gt;&gt;&nbsp;README
          </h2>
        </div>
      </div>

          <div class="Popover anim-scale-in js-tagsearch-popover" hidden="" data-tagsearch-url="/bzlee-bio/PrIMP/find-definition" data-tagsearch-ref="main" data-tagsearch-path="README" data-tagsearch-lang="Markdown" data-hydro-click="{&quot;event_type&quot;:&quot;code_navigation.click_on_symbol&quot;,&quot;payload&quot;:{&quot;action&quot;:&quot;click_on_symbol&quot;,&quot;repository_id&quot;:332645148,&quot;ref&quot;:&quot;main&quot;,&quot;language&quot;:&quot;Markdown&quot;,&quot;originating_url&quot;:&quot;https://github.com/bzlee-bio/PrIMP&quot;,&quot;user_id&quot;:19866484}}" data-hydro-click-hmac="18c667d0f98b192eef8394ade95b3364e4bbdd7635577170f4ad722d6e371990">
  <div class="Popover-message Popover-message--large Popover-message--top-left TagsearchPopover mt-1 mb-4 mx-auto Box color-shadow-large">
    <div class="TagsearchPopover-content js-tagsearch-popover-content overflow-auto" style="will-change:transform;">
    </div>
  </div>
</div>

<div data-target="readme-toc.content" class="Box-body px-5 pb-5">
<br>
<article class="markdown-body entry-content container-lg" itemprop="text"><h1>PrIMP (Prediction of ion-channel modulating peptides)</h1>
<p style="color: black;">PrIMP predicts the modulability of calcium, sodium, potassium ion channels as well as nicotine acetylcholine receptors (nAChRs) by accurate prediction models from peptide sequences only.</p>
<h2>Input file</h2>
<p style="color: black;">Input file type is fasta format in which amino acids are represented using single-letter codes.</p>
<p style="color: black;">Limitation of each sequence length is &lt;=300.</p>
<p style="color: black;">Detailed information of fasta links: <a href="https://en.wikipedia.org/wiki/FASTA_format" style="color: blue;">https://en.wikipedia.org/wiki/FASTA_format</a></p>
<h2>Output file</h2>
<p style="color: black;">Output file contains information about probabilities of four respective ion-channel modulability.</p>
<p style="color: black;">Probability with &gt;=0.5 predicts as modulator peptides for respective ion channels.</p>
</article>
        </div>
    </div>
    
			<div class="row">
				<div class="col-md-12 text-center">
					<p></p>
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
			})
		})
	</script>
</body>
</html>