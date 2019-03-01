//(function(){

    var lon;
    var lat;
    var user=new User('-1','50');
    var msg="Sorry, we were unable to get your location";
    var error_login=document.getElementById('error-msg');
    //<ul class="item-list" id="item-list">
    var itemList=document.getElementById("item-list");
    var welcomeMsg=document.querySelector(".welcome-msg");
   
    findMyLocation();
    document.querySelector("#keyterm").addEventListener("keyup", event => {
        if(event.key !== "Enter") return; // Use `.key` instead.
        document.querySelector(".keyterm-btn").click(); // Things you want to do.
        event.preventDefault(); // No need to `return false;`.
    });
    

    
    document.querySelector("#login_btn").addEventListener("click",logInCheck, false);


    function User(user_id, radius){
    	
    	this.user_id=user_id;
    	this.radius=radius;
    	this.sortByDistance=false;
    	this.sortByDate=false;
    	
    }

    function logInCheck(){
        
        var username=document.getElementById('username').value;
        var password=document.getElementById('password').value;
        password = md5(username + md5(password));
        
        //console.log(username + " "  + password);
        
        
        var url="./login";
        var xhhp=new XMLHttpRequest();
         
        xhhp.open("POST",url,true);
        
        //define the request type as JSON 
        xhhp.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        var req = JSON.stringify({
			user_id : username,
			password : password,
		});
        
        xhhp.send(req);
        xhhp.onload=function(){
            
            //console.log("response succeed");
            
            if(xhhp.status==200){
                
            	 //response a JSON
                var res=JSON.parse(xhhp.responseText);
               
                
                //successful log  in
                welcomeMsg.innerHTML=res.name;
                var logInForm=document.querySelector('.login_form');
                logInForm.style.display='none';
                var logOut=document.querySelector('#logout-link');
                logOut.style.display='inline-block';
                
                var searchForm=document.querySelector('.keyterm-container');
                searchForm.style.display='inline-block';
                user.user_id=username;
                
                var refineRadius=document.querySelector('#refine-btn');
                refineRadius.style.display='inline-block';
                document.querySelector("#radius10").addEventListener("click",function(){
                	
                	  user.radius=10;
                	  //console.log("radius: "+radius);
                	  userLoadNearBy( );
                	
                },false);       
                document.querySelector("#radius25").addEventListener("click",function(){
                	
                	user.radius=25;
              	      //console.log("radius: "+ radius);
              	     userLoadNearBy( );
              	
                },false);
                
                 document.querySelector("#radius60").addEventListener("click",function(){
                	
                	user.radius=60;
              	      //console.log("radius: "+ radius);
              	     userLoadNearBy( );
              	
                },false);
                
                document.querySelector("#radius100").addEventListener("click",function(){
                	
                	  user.radius=100;
            	      //console.log("radius: "+ radius);
            	      userLoadNearBy( );
            	
              },false);
                
                document.querySelector("#fav-btn").addEventListener("click", loadUserFavoriteItems, false);
                document.querySelector(".keyterm-btn").addEventListener("click",userLoadNearBy, false);	
                document.querySelector("#nearby-btn").addEventListener("click", userLoadNearBy, false);
                document.querySelector("#recommend-btn").addEventListener("click", userLoadRecommendedItem, false);
                
                document.querySelector("#slct").addEventListener("change",sortedSelect, false);
                document.querySelector(".select").style.display='block';
                
                
                function sortedSelect()
                {
                    var selectSorted=document.getElementById('slct');
                    var sortedValue=selectSorted.options[selectSorted.selectedIndex].text;
                    
                    if(sortedValue==="Choose an option"){
                        
                    	 //console.log("sort do nothign");
                    }
                    else if(sortedValue==="Sort By Distance"){
                        
                        console.log("distance");
                        user.sortByDistance=true;
                        user.sortByDate=false;
                        userLoadNearBy();
                        
                    }
                    else{
                        
                        console.log("date");
                        user.sortByDate=true;
                        user.sortByDistance=false;
                        userLoadNearBy();
                    }
                   
                }
                
                
                
                
                
            
            }
            else if (xhhp.status===403){
    
                 console.log("invalid session");
                 error_login.textContent="invalid session";
    
            }else{
                
                console.log("error log in");
                error_login.textContent="invalid username or password";
            }
                 
        };//end of onload
        
        
        
        xhhp.onerror=function(){
            
            
            console.error("The login fail");
            error_login.textContent="invalid username or password";
            
            
        };
            
            
        
        
        
    }//end of logInCheck

    
    //get the lon & lat
    function findMyLocation(){
        
    	
        //console.log("findMyLocation");
        if(navigator.geolocation){
                    
            navigator.geolocation.getCurrentPosition(success, fail, {
                
                timeout:5000,
                maximumAge:60000
            });
            
            itemList.innerHTML='<p class="notice"><i class="fas fa-spinner"></i> checking location...please wait...</p>';
            
        }
        else{
            
            fail(msg);
        }
        
        
        
    }
    
    
    function success(position){
        
        lon=position.coords.longitude;
        lat=position.coords.latitude;
        console.log("lon="+lon+" && lat="+lat);
        loadNearBy();
    }
    
    
    function fail(msg){
        
        itemList.innerHTML='<p class="notice">'+msg+'</p>';
        console.log("fail to get the location");
        getLocationFromIP();
        
    }
    //==================================================
    
    function activeBtn(btnId) {
    	
    	console.log("activeBtn function");
        var btns = document.getElementsByClassName('main-nav-btn');

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // active the one that has id = btnId
        var btn = document.getElementById(btnId);
        btn.className = btn.className+ ' active';
    }
    
    //=====================================================
    function loadNearBy(){
    	 
    	activeBtn('nearby-btn');
    	var keyterm=document.getElementById('keyterm').value;
        console.log("load near by event");
        var para="";
        
        document.getElementById('keyterm').value="";
        
        //document.querySelector("#slct").addEventListener("change",sortedSelect, false);
        //document.querySelector(".select").style.display='block';
        
        /*
        function sortedSelect()
        {
            var selectSorted=document.getElementById('slct');
            var sortedValue=selectSorted.options[selectSorted.selectedIndex].text;
            
            if(sortedValue==="Choose an option"){
                
            	 //console.log("sort do nothign");
            }
            else if(sortedValue==="Sort By Distance"){
                
                console.log("distance");
                user.sortByDistance=true;
                user.sortByDate=false;
                
                console.log("loadNearBy");
                loadNearBy();
                
            }
            else{
                
                console.log("date");
                user.sortByDate=true;
                user.sortByDistance=false;
                console.log("loadNearBy");
                loadNearBy();
            }
           
        }
        
        
        */
        
        
       
        //para="lon="+lon+"&lat="+lat+"&keyword="+keyterm;
        para="lon="+lon+"&lat="+lat;
        
       // console.log(para);
        keyterm="";//clear
        //http://localhost:8080/BestPlace/search?lon=-117.82&lat=34.06&keyword=sport
        var url="./search";
        //var para="lon="+lon+"&lat="+lat;
        itemList.innerHTML="";
        itemList.innerHTML='<p class="notice"><i class="fas fa-spinner"></i> checking location...please wait...</p>';
        var xhhp=new XMLHttpRequest();
        
        xhhp.open("GET",url+"?"+para,true);
        
        //define the request type as JSON 
        xhhp.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        xhhp.send();
        xhhp.onload=function(){
            
            console.log("response succeed");
            
            if(xhhp.status==200){
                
                //response a JSON
                //console.log("loadnearby res:"+xhhp.responseText);
                
                var resItems=JSON.parse(xhhp.responseText);
                
                if(!resItems||resItems.length===0){
                   
                    console.log("No nearby item");
                    itemList.innerHTML='<p class="notice"> No nearby item </p>';
                }
                else{
                    
                    //clear everything
                    itemList.innerHTML="";
                    for(var i=0; i<resItems.length; i++){
            
                        insertItem(resItems[i]);
                    }
                    
            
                }
            
            }
            else if (xhhp.status===403){
    
                 console.log("invalid session");
    
            }else{
                
                console.log("error");
            }
            
            
        };//end of onload
        
        
        
        xhhp.onerror=function(){
            
            
            console.error("The resquest couldn't be completed");
            
            
        };
        
        
    }//end of loadNearBy========================================
    
    
    function userLoadNearBy( ){
   	 
    	activeBtn('nearby-btn');
    	var keyterm=document.getElementById('keyterm').value;
        console.log("load near by event");
        var para="";
        
        document.getElementById('keyterm').value="";
        
        //console.log("inside load: "+user.radius);
        
        
        //para="lon="+lon+"&lat="+lat+"&keyword="+keyterm;
        para='user_id=' + user.user_id+"&lon="+lon+"&lat="+lat+"&keyword="+keyterm+"&radius="+user.radius+"&sortByDate="+user.sortByDate+"&sortByDistance="+user.sortByDistance;
        
        //console.log(para);
        keyterm="";//clear
        //http://localhost:8080/BestPlace/search?lon=-117.82&lat=34.06&keyword=sport
        var url="./usersearch";
        //var para="lon="+lon+"&lat="+lat;
        
        itemList.innerHTML="";
        itemList.innerHTML='<p class="notice"><i class="fas fa-spinner"></i> checking location...please wait...</p>';
        var xhhp=new XMLHttpRequest();
         
        xhhp.open("GET",url+"?"+para,true);
        
        //define the request type as JSON 
        xhhp.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        xhhp.send();
        xhhp.onload=function(){
            
            console.log("response succeed");
            
            if(xhhp.status==200){
                
                //response a JSON
                //console.log("loadnearby res:"+xhhp.responseText);
                
                var resItems=JSON.parse(xhhp.responseText);
                
                if(!resItems||resItems.length===0){
                   
                    console.log("No nearby item");
                    itemList.innerHTML='<p class="notice"> No nearby item </p>';
                }
                else{
                    
                    //clear everything
                    itemList.innerHTML="";
                    for(var i=0; i<resItems.length; i++){
            
                        insertItem(resItems[i]);
                    }
                    
            
                }
            
            }
            else if (xhhp.status===403){
    
                 console.log("invalid session");
    
            }else{
                
                console.log("error");
            }
            
            
        };//end of onload
        
        
        
        xhhp.onerror=function(){
            
            
            console.error("The resquest couldn't be completed");
            
            
        };
        
        
    }//end of userLoadNearBy========================================
    
    
    
    function loadUserFavoriteItems(){
    	
    	activeBtn('fav-btn');
    	
       // console.log("load user fav items");	
    	var url="./history";
        //var para="lon="+lon+"&lat="+lat;
        
        var para="";
        para='user_id=' + user.user_id;
        
        console.log(para);
        var xhhp=new XMLHttpRequest();
        itemList.innerHTML="";
        itemList.innerHTML='<p class="notice"><i class="fas fa-spinner"></i> Loading favorite items...</p>';
        xhhp.open("GET",url+"?"+para,true);
        
        //define the request type as JSON 
        xhhp.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        xhhp.send();
        xhhp.onload=function(){
            
            console.log("response succeed");
            
            if(xhhp.status==200){
                
                //response a JSON
                //console.log("loadnearby res:"+xhhp.responseText);
                
                var resItems=JSON.parse(xhhp.responseText);
                //clear everything
                itemList.innerHTML="";
                if(!resItems||resItems.length===0){
                   
                    console.log("No favorite item");
                    itemList.innerHTML='<p class="notice"> No favorite item</p>';
                }
                else{
                    
                    
                    for(var i=0; i<resItems.length; i++){
            
                        insertItem(resItems[i]);
                    }
                    
            
                }
            
            }
            else if (xhhp.status===403){
    
                 console.log("invalid session");
                 itemList.innerHTML='<p class="notice"> log in to save favorites items</p>';
    
            }else{
                
                console.log("error");
            }
            
            
        };//end of onload
        
        
        
        xhhp.onerror=function(){
            
            
            console.error("The resquest couldn't be completed");
            
            
        };
        
    	
    	
    	
    }//end of loadUserFavoriteItems()
    
    
  function changeFavoriteItem(item_id) {
        
        // Check whether this item has been visited or not
        var li = document.getElementById('item-'+item_id);
        var favId = document.getElementById('fav-' + item_id);
        var method='';
        if(li.dataset.favorite === 'false'){
            
            method='POST';
            
        }
        else{
            
            method='DELETE';
        }

        // The request parameters
        var url = './history';
        var req = JSON.stringify({
            user_id: user.user_id,
            favorite: item_id
        });
        
        
        var xhhp=new XMLHttpRequest();
        
        xhhp.open(method,url,true);
        
        xhhp.send(req);
        
        xhhp.onload=function(){
            
            if(xhhp.status==200){
                
                var res=JSON.parse(xhhp.responseText);
               
                if(res.result==='ok'){
                    
                    if(method==='POST'){
                    
                        li.dataset.favorite=true;
                        favId.className='fas fa-heart';
                    }
                    if(method==='DELETE'){
                    
                        li.dataset.favorite=false;
                        favId.className='far fa-heart';
                    }
                }
                
            
            }
            else{
                
                console.log("error");
            }
            
            
        };//end of onload 
        
        
        
        xhhp.onerror=function(){
            
            
            console.error("The resquest couldn't be completed");
            
            
        };
        
    }//end of changeFavoriteItem
    

   function userLoadRecommendedItem(){
   	 
    	activeBtn('recommend-btn');
    	
        console.log("recommendation");
        
        var para="";
        //para="lon="+lon+"&lat="+lat+"&keyword="+keyterm;
        para='user_id=' + user.user_id+"&lon="+lon+"&lat="+lat+"&sortByDate="+user.sortByDate+"&sortByDistance="+user.sortByDistance;
        
        //console.log(para);
        
        var xhhp=new XMLHttpRequest();
        
        var url="./userrecommendation";
        //var para="lon="+lon+"&lat="+lat;
         
        xhhp.open("GET",url+"?"+para,true);
        
        //define the request type as JSON 
        xhhp.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        itemList.innerHTML="";
        itemList.innerHTML='<p class="notice"><i class="fas fa-spinner"></i> Loading favorite items...</p>';
        
        xhhp.send();
        xhhp.onload=function(){
            
            console.log("response succeed");
            
            if(xhhp.status==200){
                
                //response a JSON
                var resItems=JSON.parse(xhhp.responseText);
                
                if(!resItems||resItems.length===0){
                   
                    console.log("No recommended item");
                    itemList.innerHTML='<p class="notice"> No recommended item. Make sure you have favorites.</p>';
                }
                else{
                    
                    //clear everything
                    itemList.innerHTML="";
                    for(var i=0; i<resItems.length; i++){
            
                        insertItem(resItems[i]);
                    }
                    
            
                }
            
            }
            else if (xhhp.status===403){
    
                 console.log("invalid session");
    
            }else{
                
                console.log("error");
            }
            
            
        };//end of onload
        
        
        
        xhhp.onerror=function(){
            
            
            console.error("The resquest couldn't be completed");
            
            
        };
        
        
    }//end of userLoadRecommendedItem========================================


    function insertItem(item){
        
        
        //create a new element
        var newLi=document.createElement('li');
        
        //<li id="item-xxx" class="item"></li>
        
        newLi.setAttribute('id','item-'+item.item_id);
        newLi.setAttribute('class','item');
        //console.log(user.user_id);
        // set the data attribute
        if(user.user_id!=='-1'){
        	
        	newLi.dataset.item_id = item.item_id;
        	newLi.dataset.favorite = item.favorite;
        }
        //image=========================================
        var image=document.createElement('img');
        
        if(item.image_url){
            
            image.setAttribute('src',item.image_url);
        }
        else{
            image.setAttribute('src','images/event.jpg');
        }
        
        newLi.appendChild(image);
        
        //itemNameContainer==================================
        var itemNameContainer=document.createElement('div');
        itemNameContainer.setAttribute('class','item-name-container');
        
        var title=document.createElement('a');
        title.setAttribute('class','item-name');
        title.setAttribute('target','_blank');
        title.setAttribute('href',item.url);
        title.innerHTML=item.name;
        
        itemNameContainer.appendChild(title);
        
        var address=document.createElement('p');
        
        address.setAttribute('class','item-address');
        address.innerHTML=item.address.replace(/\n/g, '<br/>');
        
        itemNameContainer.appendChild(address);
        
        var dates=document.createElement('p');
        dates.setAttribute('class','dates');
        dates.innerHTML=item.date;
        
        itemNameContainer.appendChild(dates);
        
        newLi.appendChild(itemNameContainer);
        
        
        //itemCategory==================================
        
        var itemCategory=document.createElement('div');
        itemCategory.setAttribute('class','item-category');
        itemCategory.innerHTML='Category:<br/>'+item.categories.join(', ');
        
        newLi.appendChild(itemCategory);
        
        //distance======================================
        var distance=document.createElement('div');
        distance.setAttribute('class','distance');
        distance.innerHTML='Distance:<br/>'+item.distance+" miles";
        
        newLi.appendChild(distance);
        
        /*
        var priceRange=document.createElement('div');
        priceRange.setAttribute('class','priceRange');
        if(item.price!='price hided'){
            
            priceRange.innerHTML='$'+item.price.replace(/~/g,'~<br/>$');
        }
        else{
            
            priceRange.innerHTML='price<br/>hided';
        }
        newLi.appendChild(priceRange);
        */
        //fav-link=====================================
        
        var favLink=document.createElement('div');
        favLink.setAttribute('class','fav-link');
        
        var faHeart=document.createElement('i');
        faHeart.setAttribute('id','fav-'+item.item_id);
        
        favLink.onclick=function(){
            
            changeFavoriteItem(item.item_id);    
        };
        
        if(item.favorite){
            
            faHeart.setAttribute('class','fas fa-heart');
            
        }
        else{
            
            faHeart.setAttribute('class','far fa-heart');
        }
        
        
        favLink.appendChild(faHeart);
        
        newLi.appendChild(favLink);
        
        //append the newLi to itemList
        
        itemList.appendChild(newLi);
        
        
    }//emd of insertItem
    
    function getLocationFromIP() {
			// Get location from http://ipinfo.io/json
			//hard coded ip
			var url = 'http://ipinfo.io/json'
			var req = null;
			
			console.log('Getting location by IP');
			var xhr = new XMLHttpRequest();
			
			xhr.open('GET', url, true);
			xhr.send();
			
			xhr.onload = function() {
				if(xhr.status === 200) {
					var result = JSON.parse(xhr.responseText);
					if ('loc' in result) {
						var loc = result.loc.split(',');
						lat = loc[0];
						lon = loc[1];
					
					} else {
						//console.log('Getting location by IP failed.');
						console.warn('Getting location by IP failed.');
					}
					
					loadNearBy();
				} else if(xhr.status === 403) {
					console.log('invalid session');
				} else {
					console.log('error');
				}
			}
			
			xhr.onerror = function() {
				console.error("The request couldn't be completed.");
				itemList.innerHTML='<p class="notice">'+'The request could not be completed.'+'</p>';
			};
	}//end of getLocationFromIP
//})();