import React from 'react';
import { StyleSheet, Text, View, NativeModules, Button } from 'react-native';


export default class App extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <View style={styles.container}>
        <Text>Click to launch the Onyx Demo from React Native</Text>
        <Button title="Demo" onPress={() => NativeModules.Onyx.navigateToOnyx()}>Onyx Demo</Button>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
