import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:geolocator/geolocator.dart';
import 'dart:ui' as ui;

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My Speed',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(

          ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key}) : super(key: key);

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  double? latitude = 0.0;
  double? longitude = 0.0;
  double? altitude = 0.0;
  double? speed = 0.0;
  DateTime? timestamp = DateTime.now();
  String status = "Pause";

  @override
  void initState() {
    runPermission();
    runLocation();
  }

  runPermission() async {
    LocationPermission permission;
    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied ||
        permission == LocationPermission.whileInUse) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.deniedForever) {
        _showDialog();
      }
    }
  }

  
  void _showDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Permissão para acessar localização negada"),
          content: const Text(
              "Acessar configurações do dispositivo e permitir acesso desse app a localização."),
          actions: <Widget>[
            TextButton(
              child: Text("OK"),
              onPressed: () async {
                Navigator.of(context).pop;
              },
            ),
          ],
        );
      },
    );
  }

  runLocation() async {
    // ignore: prefer_const_constructors
    final LocationSettings locationSettings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: 10,
    );
    StreamSubscription<Position> positionStream =
        Geolocator.getPositionStream(locationSettings: locationSettings)
            .listen((Position? position) {
      setState(() {
        latitude = position?.latitude;
        longitude = position?.longitude;
        altitude = position?.altitude;
        speed = position?.speed;
        timestamp = position?.timestamp;
      });

      print(position == null
          ? 'Unknown'
          : '${position.latitude.toString()}, ${position.longitude.toString()}');
    });
  }

  void serviceInPlatForm(String nameService) async {
    if (Platform.isAndroid) {
      var methodChannel = const MethodChannel("com.powerback.message");
      await methodChannel.invokeListMethod(nameService);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 0,
        toolbarHeight: 150,
        backgroundColor: Color.fromARGB(255, 230, 229, 229),
        centerTitle: true,
        title: GradientText(
          'My Speed',
          style: const TextStyle(fontSize: 40),
          gradient: LinearGradient(colors: [
            Colors.blue.shade400,
            Color.fromARGB(255, 144, 13, 161),
          ]),
        ),
      ),
      body: Center(
          child: Container(
              child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: ListView(children: [
                    //Latitude Card
                    Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                        children: [
                          ListTile(
                            leading: const RotatedBox(
                              quarterTurns: 1,
                              child: Icon(Icons.height_rounded),
                            ),
                            title: const Text('Latitude'),
                            subtitle: Text(
                              '$latitude',
                              style: TextStyle(
                                  color: Colors.black.withOpacity(0.6)),
                            ),
                          ),
                        ],
                      ),
                    ),
                    //Longitude Card
                    Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                        children: [
                          ListTile(
                            leading: const Icon(Icons.height_rounded),
                            title: const Text('Longitude'),
                            subtitle: Text(
                              '$longitude',
                              style: TextStyle(
                                  color: Colors.black.withOpacity(0.6)),
                            ),
                          ),
                        ],
                      ),
                    ),
                    //Altitude Card
                    Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                        children: [
                          ListTile(
                            leading: const Icon(Icons.terrain),
                            title: const Text('Altitude'),
                            subtitle: Text(
                              '$altitude',
                              style: TextStyle(
                                  color: Colors.black.withOpacity(0.6)),
                            ),
                          ),
                        ],
                      ),
                    ),
                    //speed Card
                    Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                        children: [
                          ListTile(
                            leading: const Icon(Icons.speed),
                            title: const Text('Velocidade'),
                            subtitle: Text(
                              "${(speed! * 3.6)} km/h",
                              style: TextStyle(
                                  color: Colors.black.withOpacity(0.6)),
                            ),
                          ),
                        ],
                      ),
                    ),
                    //Date Time Card
                    Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                        children: [
                          ListTile(
                            leading: const Icon(Icons.access_time),
                            title: const Text('Data & Hora'),
                            subtitle: Text(
                              '$timestamp',
                              style: TextStyle(
                                  color: Colors.black.withOpacity(0.6)),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ])))),
    );
  }
}

class GradientText extends StatelessWidget {
  const GradientText(
    this.text, {
    required this.gradient,
    this.style,
  });

  final String text;
  final TextStyle? style;
  final Gradient gradient;

  @override
  Widget build(BuildContext context) {
    return ShaderMask(
      blendMode: BlendMode.srcIn,
      shaderCallback: (bounds) => gradient.createShader(
        Rect.fromLTWH(0, 0, bounds.width, bounds.height),
      ),
      child: Text(text, style: style),
    );
  }
}
