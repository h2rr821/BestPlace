var subReigster=document.getElementById('subReigster');
var newAccount=document.getElementById('newAccount');

var username=document.getElementById('username');
var password=document.getElementById('password');
var firstname=document.getElementById('firstname');
var lastname=document.getElementById('lastname');
var elEmpty=document.querySelector("#errorEmpty");
	
username.addEventListener('blur', function(){
		
   checkUserName(3);

},false);

function checkUserName(minLength){
	
	var elUser=document.querySelector("#errorUserName");
	
	if (username.value.length < minLength) 
	{    
	
		elUser.textContent='user name have to be ' +minLength+' characters or more';
	}
	else
	{
		elUser.textContent='';
	}
	
}

password.addEventListener('blur', function(){
		
   checkPassword(5);

},false);

function checkPassword(minLength){
	
	var elPassword=document.querySelector("#errorPassword");
	
	if (password.value.length < minLength) 
	{    
	
		elPassword.textContent='password have to be ' +minLength+' characters or more';
	}
	else
	{
		elPassword.textContent='';
	}
	
}


function register(){
    

 
     var usernameValue=username.value;
     var passwordValue=password.value;
     var firstnameValue=firstname.value;
     var lastnameValue=lastname.value;
    
     
     if(usernameValue.length<3 ||
        passwordValue.length<5 ||
        firstnameValue.length===0 ||
        lastnameValue.length===0){
         
         errorEmpty.textContent='input incorrectly';
         
     }
     else{
    
    
     passwordValue = md5(usernameValue + md5(passwordValue));
     
     
     
     document.getElementById('username').value="";
     document.getElementById('password').value="";
     document.getElementById('firstname').value="";
     document.getElementById('lastname').value="";
     
   
     var url="./register";
     var xhhp=new XMLHttpRequest();
         
     xhhp.open("POST",url,true);
        
      //define the request type as JSON 
     xhhp.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        
     var req = JSON.stringify({
			user_id : usernameValue,
			password : passwordValue,
            firstname: firstnameValue,
            lastname:lastnameValue,
	 });
        
     xhhp.send(req);
     xhhp.onload=function(){
            
      //console.log("response succeed");
            
        if(xhhp.status==200){
                
          //response a JSON
           var res=JSON.parse(xhhp.responseText);
           //console.log("correct username and password");
           newAccount.innerHTML=res.result;
            
       }
        else{
                
              console.log("error register");
        }
                 
     };//end of onload
     
        
        
    xhhp.onerror=function(){
            
            
            console.error("register fail");
            
            
     };     
   }
}//end of register
       


subReigster.addEventListener("click",register,false);

