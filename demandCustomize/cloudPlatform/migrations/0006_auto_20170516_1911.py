# -*- coding: utf-8 -*-
# Generated by Django 1.10.4 on 2017-05-16 19:11
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('cloudPlatform', '0005_auto_20170516_1717'),
    ]

    operations = [
        migrations.AlterField(
            model_name='resultfile',
            name='ResultFileID',
            field=models.CharField(max_length=45, primary_key=True, serialize=False),
        ),
    ]