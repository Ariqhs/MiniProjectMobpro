<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Diary extends Model
{
    use HasFactory;
    protected $fillable = [
        'user_email',
        'title',
        'foreign_amount',
        'currency_code',
        'converted_idr',
        'image_path'
    ];
}
