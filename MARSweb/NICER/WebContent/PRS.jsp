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
						data-aos-delay="500">PRS</h1><br>
					<h2 class="h5 ftco-subheading mb-5" data-aos="fade-up"
						data-aos-delay="600">
						<br> 
						<br><br><br><br>
					</h2>

				</div>
			</div>
		</div>
	</section>


	<br>
	<br>
	<div class="container" style="background:#f5f5f5;">
		<div class="col-md-7">
			<h1 data-aos="fade-up" data-aos-delay="500">CVD Predict System</h1>
			<h2 data-aos="fade-up" data-aos-delay="600" style="width:1000px;">Upload PRS(Polgenic Risk Score) file and additional information<br>&nbsp;</br></h2>
		</div>

		<footer class="ftco-footer ftco-bg-dark ftco-section">
			<div class="ftco-section contact-section">
				<div class="container">
					<div class="row block-9">
						<div class="col-md-6 pr-md-5" style="background-image: none;">
								<input id="tab1" type="radio" name="tabs" value="type1"
								checked="checked" style="display: none"> <label for="tab1"
								data-tab="tab-1" style="padding: 15px 18px">CVD Risk Prediction</label> 

							<div id="tab-1" class="tab-content current">
								<form action="PRSServlet" onsubmit="submitForm(event)" method="post"
									enctype="multipart/form-data">

									<div class="form-group">
										<table cellspacing="100%">
											<tr>
												<th cellspacing="50%"><input type="text" class="form-control" name="userName"
													placeholder="Name"></th>
							                    <th><input type="number" class="form-control" name="age" placeholder="Age" min="1" max="100"></th>
											</tr>
										</table>
									</div>
									<input type="text" name="tabss" value="type1"
										style="display: none">
									<div class="form-group">
										<table cellspacing="100%">
											<tr>
												<th cellspacing="50%"><input type="text"
													placeholder=" Upload Score file" disabled ></th>
												<th><input type="file" name="score_file"></th>
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
						<div class="col-md-6 pr-md-5" style="background-image: url(images/back_image1.png); background-size: cover;">
						<label for="tab1" data-tab="tab-1" style="padding: 15px 18px; width:100%;pointer-events: none; color:black;"></label>

						</div>
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
    </div>
    
			<div class="row">
				<div class="col-md-12 text-center">
					 <div id="imagesContainer">???????????</div>
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
    <script>
	    function submitForm(event) {
	        event.preventDefault(); // 폼의 기본 제출 동작을 막음
	
	        console.log("Form submission started.");
			
	        var form = document.querySelector("form");
	        var formData = new FormData(form);
	
	        var xhr = new XMLHttpRequest();
	        xhr.open("POST", form.action, true); // form.action에서 URL을 가져옴
	
	        xhr.onreadystatechange = function() {
	            if (xhr.readyState === 4) {
	                console.log("Request completed with status: " + xhr.status);
	                if (xhr.status === 200) {
	                    // 서버 응답을 성공적으로 받았을 때 처리
	                    var response = JSON.parse(xhr.responseText);
	                    var currentTime = response.currentTime;
	                    var imageUrls = response.imageUrls;
	                    var imageGrid = document.createElement('div');
//	                    imageGrid.className = 'image-grid';

	                    imageUrls.forEach(function(url) {
	                        var img = document.createElement('img');
	                        img.src = url;
	                        img.alt = "Image";
	                        imageGrid.appendChild(img);
	                    });
	                    
	                    var container = document.getElementById('imagesContainer');
	                    container.innerHTML = xhr.responseText; // Clear previous content
	                    container.appendChild(imageGrid);
	                    console.log(xhr.responseText); // 응답 내용을 로그로 출력
	                    console.log(response.imageUrls); // 이미지 URL 배열 로그 출력
	                } else {
	                    console.error("Error in request. Status: " + xhr.status);
	                }
	            }
	        };
	
	        // 요청 전송
	        console.log("Sending request to: " + form.action);
	        xhr.send(formData);
	    }
	    
    </script>
</body>
</html>