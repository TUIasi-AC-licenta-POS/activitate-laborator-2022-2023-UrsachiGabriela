import React from "react";
import axios from "axios";

class Songs extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            songs : null,
            errorMessage: null
        };
    }

    componentDidMount() {
        // Simple GET request using axios
        const headers = {
            'Authorization': 'Bearer my-token',
            'My-Custom-Header': 'foobar'
        };

        axios.get('http://localhost:8080/api/songcollection/songs/66')
            .then(response => this.setState({ songs: response.data.name }))
            .catch(error => {
                this.setState({errorMessage: error.message});
                console.log("There was an error", error);
            });
            
    }


    render() {
        const { songs } = this.state;
        const {errorMessage} = this.state;
        return (
            <div className="card text-center m-3">
                <h5 className="card-header">Simple GET Request</h5>
                <div className="card-body">
                    Total react packages: {songs}
                </div>

                <div className="card-body">
                    Error message: {errorMessage} </div>
            </div>
        );
    }
}

export default Songs;