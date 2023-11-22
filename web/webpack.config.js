const path = require('path');
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CssMinimizerWebpackPlugin = require('css-minimizer-webpack-plugin');
const TerserPlugin = require("terser-webpack-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const CompressionPlugin = require('compression-webpack-plugin');
// const BundleAnalyzerPlugin = require("webpack-bundle-analyzer").BundleAnalyzerPlugin;
const WebpackBar = require('webpackbar');
const xdcore = require("xdcorelib/webapck.xdcore");
const merge = require('webpack-merge');
const CopyWebpackPlugin = require('copy-webpack-plugin');

let input = "./src/index.tsx";
let xdcorelib = "node_modules/xdcorelib/dist";
module.exports = (env) => {
    if (env && env.web) {//build
        const config = {
            mode: 'production',
            entry: {
                index: {
                    import: input,
                },
            },
            output: {
                filename: '[name].[contenthash].js',
                path: path.resolve(__dirname, 'dist'),
                clean: true,
                publicPath: './',
            },
            // devtool: 'hidden-source-map',
            devServer: {
                static: './public'
            },
            plugins: [
                new webpack.ProvidePlugin({
                    process: 'process/browser',
                }),
                new CopyWebpackPlugin({
                    patterns: [
                        {from: "public", to: ""},
                        {from: xdcorelib, to: "xdcore"},
                    ]
                }),
                new WebpackBar(),
                new HtmlWebpackPlugin({
                    title: 'xdcoreweb',
                    template: "./src/index.html",     //path.resolve(__dirname, "./src/index.html"),
                }),
                new MiniCssExtractPlugin({
                    // Options similar to the same options in webpackOptions.output
                    // all options are optional
                    filename: "[name].css",
                    chunkFilename: "[id].css",
                    ignoreOrder: false, // Enable to remove warnings about conflicting order
                }),
                new xdcore(),
            ],
            optimization: {
                minimizer: [
                    new TerserPlugin({
                        terserOptions: {
                            compress: {
                                drop_console: true, // eslint-disable-line @typescript-eslint/camelcase
                                warnings: true,
                            },
                        },
                    }),
                    new OptimizeCSSAssetsPlugin({}),
                    // new CssMinimizerWebpackPlugin({})
                ],
                // moduleIds: false,   //named   hashed   deterministic
            },
        };
        return merge(commonConfig, config);
    } else {
        const config = {
            mode: 'development',
            entry: {
                index: {
                    import: input,
                },
            },
            devtool: 'inline-source-map',
            devServer: {
                static: './public'
            },
            plugins: [
                new webpack.ProvidePlugin({
                    process: 'process/browser',
                }),
                // new MonacoWebpackPlugin([]),
                new CopyWebpackPlugin({
                    patterns: [
                        {from: "public", to: ""},
                        {from: xdcorelib, to: "xdcore"},
                    ]
                }),
                new WebpackBar(),
                new HtmlWebpackPlugin({
                    title: 'xdcoreweb',
                    template: "./src/index.html",     //path.resolve(__dirname, "./src/index.html"),
                }),
                new MiniCssExtractPlugin({
                    // Options similar to the same options in webpackOptions.output
                    // all options are optional
                    filename: "[name].css",
                    chunkFilename: "[id].css",
                    ignoreOrder: false, // Enable to remove warnings about conflicting order
                }),
                new xdcore(),
            ],
            optimization: {
                minimizer: [new CssMinimizerWebpackPlugin({})],
                moduleIds: 'deterministic',   //named   hashed   deterministic
                // runtimeChunk: 'single',
            },
        }
        return merge(commonConfig, config);
    }
};
const commonConfig = {
    output: {
        filename: '[name].[contenthash].js',
        path: path.resolve(__dirname, 'dist'),
        clean: true,
        publicPath: '/',
    },
    performance: {
        maxEntrypointSize: 10000000,
        maxAssetSize: 30000000,
    },
    resolve: {
        extensions: ['.tsx', '.jsx', '.ts', '.js'],
        alias: {
            "@": path.resolve(__dirname, "./src"),
        },
    },
    module: {
        rules: [
            {test: /\.m?js/, resolve: {fullySpecified: false,}},
            {
                test: /\.less$/i,
                use: [
                    // compiles Less to CSS
                    'style-loader',
                    'css-loader',
                    {
                        loader: 'less-loader',
                        options: {
                            lessOptions: {
                                javascriptEnabled: true
                            }
                        }
                    },
                ],
            },
            {
                test: /\.(js|tsx|jsx)?$/,
                use: [
                    {
                        loader: 'babel-loader',
                        options: {
                            presets: [
                                ['@babel/preset-typescript', {allowDeclareFields: true}],
                                "@babel/preset-env",
                                ["@babel/preset-react", {
                                    "runtime": "automatic",
                                    "throwIfNamespace":false
                                }]
                            ],
                            plugins: [
                                [
                                    "@babel/plugin-proposal-decorators",
                                    {
                                        "legacy": true
                                    }
                                ]
                            ]
                            //生产环境下不需要HRM热模块更新，所以去掉
                        }
                    },
                ],
                exclude: /node_modules/,
            },
            {
                test: /\.css$/i,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {
                            // you can specify a publicPath here
                            // by default it uses publicPath in webpackOptions.output
                            publicPath: "../",
                        },
                    },
                    "css-loader",
                ],
            },
            {
                test: /\.(png|svg|jpg|jpeg|gif|xml)$/i,
                type: 'asset/resource',
            },
            {
                test: /\.tsx?$/,
                use: [
                    {
                        loader: 'ts-loader',
                        options: {
                            transpileOnly: true,
                        },
                    },
                ],
                exclude: /node_modules/,
            }
        ],
    },
}
