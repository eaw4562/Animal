# Animal
코틀린으로 작성하고 있는 동물 분양 어플리케이션 입니다.
프래그먼트를 이용해 동물 분양을 하는 게시판 과 동물보호소 2개의 탭을 가진 메인화면을 구성했습니다.
로그인은 FirebaseAuth를 이용했으며, 현재 email 로그인만 구현되어있고 추후 구글로그인 추가 예정입니다.
로그인을 한후 게시글 작성을 누르면 BoardWrite Activity로 이동해 게시글을 작성할 수 있으며 게시글 내용과 최대 10장의 사진을 첨부할 수 있습니다.
게시글 저장은 FirebaseStorage와 Firestore를 사용했고 다중 이미지를 업로드함에 있어 이미지 사이즈 이슈가 있어 Bitmap.compress를 사용해 이미지의 리사이징을 구현했습니다.
저장된 게시글을 홈의 동물 분양 탭에 리사이클러뷰를 활용해 표시할 예정입니다. 