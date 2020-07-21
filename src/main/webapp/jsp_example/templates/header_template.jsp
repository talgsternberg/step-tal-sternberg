<jsp:scriptlet>
    boolean loggedIn = false;
    if((int) (Math.random() * 2) == 1) { 
        loggedIn = true;
    }
</jsp:scriptlet>
<div>
    HEADER LINKS GO HERE - 
    <jsp:expression>
        loggedIn ? "Logged In!" : "Not Logged In!"
    </jsp:expression>
</div>