<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\DiaryController;

Route::get('/diaries', [DiaryController::class, 'index']);
Route::post('/diaries', [DiaryController::class, 'store']);
Route::delete('/diaries/{id}', [DiaryController::class, 'destroy']);
