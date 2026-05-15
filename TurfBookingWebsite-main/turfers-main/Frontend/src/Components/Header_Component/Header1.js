import React from 'react'

function Header1(props) {
    let turferclick = (event) => {
        event.preventDefault();
        if(props.cred.tokenid!=="" && props.cred.type==="Admin")
        {
            props.setview("Homepage");
        }
        else if(props.cred.tokenid!=="" && props.cred.type==="User")
        {
            props.setview("Homepage");
        }
        else
        {
            window.location.href = "/";
        }
    }
    return (
        <>
        <nav className="navbar navbar-inverse">
            <div className="container-fluid">
                <div className="navbar-header">
                    <a className="navbar-brand" href="/" onClick={turferclick}>Turfers</a>
                </div>
                {(props.cred.tokenid!=="") &&
                <ul className="nav navbar-nav navbar-right">
                    <li><span className="navbar-text"><span className="glyphicon glyphicon-user"></span> Welcome, {props.cred.userid}</span></li>
                    <li><a href="/"><span className="glyphicon glyphicon-log-in"></span> Logout</a></li>
                </ul>
                }
            </div>
        </nav>
        </>
        );
}
export default Header1;
