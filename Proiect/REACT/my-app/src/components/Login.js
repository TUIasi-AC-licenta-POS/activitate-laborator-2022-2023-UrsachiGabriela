import React, { Fragment } from "react"
import axios from "axios";

class Login extends React.Component {
    constructor(props){
        super(props);

        this.state = {
            username: "",
            password:"",
            status : null,
            token: null
        };
    }

    handleChangeEvents=(event) =>{
       //this.setState({username: event.target.value});
    }

    handleSubmitevents=(event) =>{
        this.setState({username: document.getElementById("uname").value, password: document.getElementById("upass").value});
     

                        //password: document.getElementById("upass").value});
        // handle submit events
    }

    handlePasswordChange=(event)=>{
       //this.setState({password: event.target.value});
       //console.log("P "+event.target.value)
    }
       
    componentDidUpdate(){
        console.log(this.state.username)
        console.log(this.state.password)
        //console.log(this.state.username)
    }

    render() {
        return (
            <Fragment>
            <form>
                <label>User Name</label>
                <input type="text" id="uname" data-test="username" onChange={this.handleChangeEvents} />
                
                <label>Password</label>
                <input type="password" id="upass" data-test="password"  onChange={this.handlePasswordChange } />
                
                <button value="Log in" onClick={this.handleSubmitevents}></button>
                {/* <input type="submit" value="Log In" data-test="submit" /> */}
            </form>

            <p>TOKEN: {this.state.token}</p>
            </Fragment>

        )
  }

  componentDidMount() {

    // Simple POST request with a JSON body using axios
    const loginRequest = { 
        name: 'admin',
        password: 'admin' 
    };
    axios.post('http://localhost:8082/api/spotify/login', loginRequest)
        .then(response => this.setState({ token: response.data.jwsToken }));
}
}
export default Login